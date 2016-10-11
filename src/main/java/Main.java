import static spark.Spark.*;

import field.*;
import generator.*;
import sensor.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Main {

    private static Map<String,AbstractSensor> sensors = new HashMap<String,AbstractSensor>();


    public static void main(String[] args) {

        initialize();
        get("/get/:sensorname", (request, response) -> {
            JSONObject obj = new JSONObject();
            String requested_sensor = request.params(":sensorname");
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
     * Read config and setup sensor repository
     */
    private static void initialize() {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader("config.json"));
            JSONObject jsonObject = (JSONObject) obj;

            JSONArray json_sensors = (JSONArray) jsonObject.get("sensors");
            Iterator<JSONObject> sensor_iterator = json_sensors.iterator();
            while (sensor_iterator.hasNext()) {
                JSONObject sensor = sensor_iterator.next();
                DefaultSensor s = new DefaultSensor((String) sensor.get("id")); // init sensor object
                JSONArray json_fields = (JSONArray) sensor.get("fields");
                Iterator<JSONObject> iterator = json_fields.iterator();
                while (iterator.hasNext()) { // add fields to sensor object
                    JSONObject field = iterator.next();
                    // load adequate field
                    Class<?> field_class = Class.forName("field."+(String) field.get("type"));
                    Constructor<?> field_constructor = field_class.getConstructor(String.class);
                    AbstractField f = (AbstractField) field_constructor.newInstance((String) field.get("name"));

                    // load adequate generator
                    AbstractGenerator g;
                    try {
                        Class<?> generator_class = Class.forName("generator."+(String) field.get("generator"));
                        Constructor<?> generator_constructor = field_class.getConstructor(JSONObject.class);
                        g = (AbstractGenerator) generator_constructor.newInstance((JSONObject) field);
                    } catch(Exception e) { // if no class was found load default generator
                        g = new DefaultGenerator((JSONObject) field);
                    }
                    f.setGenerator(g);
                    s.getFields().add(f);
                }
                sensors.put((String) sensor.get("id"), s); // add sensor object to sensor repository
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
