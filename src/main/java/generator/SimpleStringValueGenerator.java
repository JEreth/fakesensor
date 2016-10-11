package generator;


import org.json.simple.JSONObject;

/**
 * Simple string generator that just retuns a static string value
 */
public class SimpleStringValueGenerator extends AbstractGenerator<String> {

    private String value;

    public SimpleStringValueGenerator(JSONObject field) {
        super(field);
        this.value = (String) field.get("value");
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String generateValue() {

        return value;
    }

}
