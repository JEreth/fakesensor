package field;

/**
 * A simple string field that returns a text
 *
 */
public class StringField extends AbstractField<String> {

    public StringField(String name) {
        this.name=name;
        this.type = "String";
    }
}
