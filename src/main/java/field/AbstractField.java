package field;


import generator.AbstractGenerator;

/**
 * AbstractGenerator class of a field that contains minimal setup
 */
public abstract class AbstractField<T> {

    protected String name;
    protected String type;
    protected AbstractGenerator generator;

    public T generateValue() {
      return (T) generator.generateValue();
    };

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public AbstractGenerator getGenerator() {
        return generator;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setGenerator(AbstractGenerator generator) {
        this.generator = generator;
    }
}
