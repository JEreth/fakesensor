package generator;

import org.json.simple.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;


/**
 * Generator returns double between a given range
 */
public class SimpleRangeDoubleGenerator extends AbstractGenerator<Double> {

    private Double range_from = 0.0;
    private Double range_to = 100.0;
    private Integer decimals = 2;

    public SimpleRangeDoubleGenerator(JSONObject field) {
        super(field);
        Double range_from = Double.parseDouble((String) field.get("range_from"));
        Double range_to = Double.parseDouble((String) field.get("range_to"));
        this.range_from = range_from;
        this.range_to = range_to;
        if (field.get("decimal_places")!= null) {
            this.decimals = Integer.parseInt((String) field.get("decimal_places"));
        }
    }

    public Double generateValue() {
        return round(ThreadLocalRandom.current().nextDouble(range_from, range_to));
    }

    public Double getRangeFrom() {
        return range_from;
    }

    public Double getRangeTo() {
        return range_to;
    }

    public void setRangeTo(Double range_to) {
        this.range_to = range_to;
    }

    public void setRangeFrom(Double range_from) {
        this.range_from = range_from;
    }

    private double round(double value) {
        if (decimals < 0) decimals = 2; // default handler
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(decimals, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
