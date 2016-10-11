package generator;

import org.json.simple.JSONObject;

/**
 * AbstractGenerator generator
 */
public abstract class AbstractGenerator<T> {

    public AbstractGenerator(JSONObject field) {
        // parse field and init generator
    }

    public abstract T generateValue();

}
