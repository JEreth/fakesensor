package generator;

import org.json.simple.JSONObject;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Fallback generator that simply returns random Integer between 0 and 100
 */
public class DefaultGenerator extends AbstractGenerator<Integer> {


    public DefaultGenerator(JSONObject field) {
        super(field);
    }

    public Integer generateValue() {
        return ThreadLocalRandom.current().nextInt(0, 100 + 1);
    }

}
