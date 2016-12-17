import static spark.Spark.*;

import field.*;
import generator.*;
import helper.CsvWriter;
import sensor.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.text.SimpleDateFormat;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.InterceptHandler;
import io.moquette.interception.messages.InterceptPublishMessage;
import io.moquette.server.Server;
import io.moquette.server.config.ClasspathConfig;
import io.moquette.server.config.IConfig;

public class Main {

    private static Map<String,AbstractSensor> sensors = new HashMap<String,AbstractSensor>();

    private static String mqtt_broker = "tcp://0.0.0.0:1883";

    // helper to print out published messages (for testing)
    static class PublisherListener extends AbstractInterceptHandler {
        @Override
        public void onPublish(InterceptPublishMessage message) {
            System.out.println("moquette mqtt broker message intercepted, topic: " + message.getTopicName()
                    + ", content: " + new String(message.getPayload().array()));
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {

        // init depending on mode
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader("config.json"));
            JSONObject jsonObject = (JSONObject) obj;

            // INIT SENSORS
            initSensors(jsonObject);

            // CHECK MODE FOR COMMUNICATING EVENTS - default is csv
            switch ((String) jsonObject.get("mode")) {
                case "mqtt":
                    initMqtt(jsonObject);
                    break;
                case "http":
                    initHttp(jsonObject);
                    break;
                default:
                    initCsv(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace(); // something went southwards
        }
    }

    /**
     *
     * @param config
     */
    private static void initSensors(JSONObject config) {
        JSONArray json_sensors = (JSONArray) config.get("sensors");
        for (Object sensorObj : json_sensors) {
            JSONObject sensor = (JSONObject)sensorObj;
            DefaultSensor s = new DefaultSensor((String) sensor.get("id")); // init sensor object

            // check if publish sensor
            if (sensor.get("publish_interval") != null) {
                s.setPublishInterval((int) (long) sensor.get("publish_interval"));
            }

            JSONArray json_fields = (JSONArray) sensor.get("fields");
            for (Object jsonFieldObj : json_fields) {
                try {
                    JSONObject field = (JSONObject)jsonFieldObj;
                    // load adequate field
                    Class<?> field_class = Class.forName("field." + (String) field.get("type"));
                    Constructor<?> field_constructor = field_class.getConstructor(String.class);
                    AbstractField f = (AbstractField) field_constructor.newInstance((String) field.get("name"));

                    // load adequate generator
                    AbstractGenerator g;
                    try {
                        Class<?> generator_class = Class.forName("generator." + (String) field.get("generator"));
                        Constructor<?> generator_constructor = generator_class.getConstructor(JSONObject.class);
                        g = (AbstractGenerator) generator_constructor.newInstance((JSONObject) field);
                    } catch (Exception e) { // if no class was found load default generator
                        g = new DefaultGenerator((JSONObject) field);
                    }
                    f.setGenerator(g);
                    s.getFields().add(f);
                } catch(Exception e) { // something didn't work out just skip this field
                    e.printStackTrace(); // something went southwards
                }
            }
            sensors.put((String) sensor.get("id"), s); // add sensor object to sensor repository
        }
    }

    /**
     * Init http mode to publish events per http requests
     * @param config coming from config.json
     */
    private static void initHttp(JSONObject config) {

        get("/get/:sensorname", (request, response) -> {
            JSONObject obj = new JSONObject();
            String requested_sensor = request.params(":sensorname");
            System.out.println("Request for "+requested_sensor);
            obj.put("timestamp",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
            if (sensors.containsKey(requested_sensor)) { // check if sensor exists
                obj.put("status", "success");
                JSONArray response_fields = new JSONArray();
                AbstractSensor s = sensors.get(requested_sensor);
                JSONObject field_obj = new JSONObject();
                for(AbstractField f : s.getFields()) {
                    field_obj.put((String) f.getName(), f.generateValue());
                }
                response_fields.add(field_obj);
                obj.put("response", response_fields);
            } else { // if not return 404
                obj.put("status", "error");
                obj.put("msg", "sensor not found");
                response.status(404);
            }
            return (String) obj.toJSONString(); // return as json
        });

    }

    /**
     * Init mqtt mode to regularly publish events via mqtt
     * @param config coming from config.json
     */
    private static void initMqtt(JSONObject config) throws IOException {

        if (config.get("mqtt_broker")!= null) { // if custom broker was set
            mqtt_broker = (String) config.get("mqtt_broker");
        }

        // INIT MQTT BROKER (MOQUETTE)
        final IConfig classPathConfig = new ClasspathConfig();

        final Server mqttBroker = new Server();
        final List<? extends InterceptHandler> userHandlers = Arrays.asList(new PublisherListener());
        mqttBroker.startServer(classPathConfig, userHandlers);

        System.out.println("moquette mqtt broker started, press ctrl-c to shutdown..");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("stopping moquette mqtt broker..");
                mqttBroker.stopServer();
                System.out.println("moquette mqtt broker stopped");
            }
        });

        // INIT MQTT client (PAHO)
        int qos = 2;
        String clientId = "fake-paho-client";
        try {
            // CONNECT TO BROKER
            MqttClient fakeClient = new MqttClient(mqtt_broker, clientId, new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to mqtt broker: " + mqtt_broker);
            fakeClient.connect(connOpts);
            java.util.Timer t = new java.util.Timer();
            for (AbstractSensor sensor : sensors.values()) {
                if (sensor.getPublishInterval()>0) {
                    t.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // generate json for publishing
                            JSONObject obj = new JSONObject();
                            obj.put("timestamp",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
                            obj.put("status", "success");
                            JSONArray response_fields = new JSONArray();
                            JSONObject field_obj = new JSONObject();
                            for(AbstractField f : sensor.getFields()) {
                                field_obj.put((String) f.getName(), f.generateValue());
                            }
                            response_fields.add(field_obj);
                            obj.put("response", response_fields);
                            try {
                                MqttMessage message = new MqttMessage(obj.toJSONString().getBytes());
                                message.setQos(qos);
                                fakeClient.publish(sensor.getName(), message);
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }
                            //fakeClient.disconnect();
                            //System.out.println);
                        }
                    }, sensor.getPublishInterval(), sensor.getPublishInterval());
                }
            }
        } catch (MqttException me) {
            me.printStackTrace();
        }
    }

    /**
     * Init csv mode to regularly write events to csv
     * @param config coming from config.json
     */
    private static void initCsv(JSONObject config) {
        System.out.println("Running in CSV mode ...");
        CsvWriter w = new CsvWriter();

        java.util.Timer t = new java.util.Timer();
        sensors.values().stream().filter(sensor -> sensor.getPublishInterval() > 0).forEach(sensor -> {
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    HashMap<String, String> valueMap = new HashMap<String, String>();
                    valueMap.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
                    valueMap.put("status", "success");
                    for (AbstractField f : sensor.getFields()) {
                        valueMap.put((String) f.getName(), String.valueOf(f.generateValue()));
                    }
                    w.writLine(sensor.getName(), valueMap);
                }
            }, sensor.getPublishInterval(), sensor.getPublishInterval());
        });
    }

}
