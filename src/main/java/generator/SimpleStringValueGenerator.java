package generator;


/**
 * Simple string generator that just retuns a static string value
 */
public class SimpleStringValueGenerator extends AbstractGenerator<String> {

    private String value;

    public SimpleStringValueGenerator(String value) {
        this.value = value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String generateValue() {

        return value;
    }

}
