import static spark.Spark.*;

import field.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Main {

    private static String config;
    private static ArrayList<field.Abstract> fields = new ArrayList<field.Abstract>();


    public static void main(String[] args) {

        initialize();
        JSONObject obj = new JSONObject();

        for(field.Abstract f : fields) {
            obj.put((String) f.getName(), f.generateValue());
        }

        get("/get", (req, res) -> (String) obj.toJSONString());
    }

    /**
     * read config and setup fields
     */
    private static void initialize() {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader("config.json"));
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray json_fields = (JSONArray) jsonObject.get("fields");
            Iterator<JSONObject> iterator = json_fields.iterator();
            while (iterator.hasNext()) {
                JSONObject field = iterator.next();
                FieldInt f = new FieldInt((String) field.get("name"));
                fields.add(f);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a set of values according to the confix.xml setup
     * @return
     */
    public String getValue() {
        return "Test";
    }
}
