package sensor;

import field.AbstractField;
import java.util.ArrayList;


public abstract class AbstractSensor {

    protected ArrayList<AbstractField> fields = new ArrayList<AbstractField>();
    protected String name;
    protected int publish_interval = 0;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPublishInterval() {
        return publish_interval;
    }

    public void setPublishInterval(int publish_interval) {
        this.publish_interval = publish_interval;
    }


    public ArrayList<AbstractField> getFields() {
        return this.fields;
    }

    public void setFields(ArrayList<AbstractField> fields) {
        this.fields = fields;
    }

}
