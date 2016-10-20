package field;

/**
 * A simple integer field that returns a full number
 *
 */
public class DoubleField extends AbstractField<Double> {

    public DoubleField(String name) {
        this.name=name;
        this.type = "Double";
    }
}
