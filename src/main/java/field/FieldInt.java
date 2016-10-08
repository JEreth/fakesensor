package field;

/**
 * Created by Julian on 08.10.2016.
 */
public class FieldInt extends Abstract<Integer> {

    public Integer generateValue() {
        return 99;
    }

    public FieldInt(String name) {
        this.name=name;
        this.type = "Integer";
    }
}
