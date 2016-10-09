package generator;

import java.util.concurrent.ThreadLocalRandom;


/**
 * Generator returns integer between a given range
 */
public class SimpleRangeIntegerGenerator extends AbstractGenerator<Integer> {

    private Integer range_from = 0;
    private Integer range_to = 100;

    public SimpleRangeIntegerGenerator(Integer min, Integer max) {
        this.range_from = min;
        this.range_to = max;
    }

    public Integer generateValue() {
        return ThreadLocalRandom.current().nextInt(range_from, range_to + 1);
    }

    public Integer getRangeFrom() {
        return range_from;
    }

    public Integer getRangeTo() {
        return range_to;
    }

    public void setRangeTo(Integer range_to) {
        this.range_to = range_to;
    }

    public void setRangeFrom(Integer range_from) {
        this.range_from = range_from;
    }
}
