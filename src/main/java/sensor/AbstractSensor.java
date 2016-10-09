package sensor;

import field.AbstractField;
import java.util.ArrayList;


public abstract class AbstractSensor {

    protected ArrayList<AbstractField> fields = new ArrayList<AbstractField>();
    protected String name;

    public ArrayList<AbstractField> getFields() {
        return this.fields;
    }

    public void setFields(ArrayList<AbstractField> fields) {
        this.fields = fields;
    }

}
