package field;

/**
 * A simple integer field that returns a full number
 *
 */
public class IntegerField extends AbstractField<Integer> {

    public IntegerField(String name) {
        this.name=name;
        this.type = "Integer";
    }
}
