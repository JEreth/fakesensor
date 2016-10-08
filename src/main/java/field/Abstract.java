package field;


/**
 * Abstract class of a field that contains minimal setup
 */
public abstract class Abstract<T> {

    protected String name;
    protected String type;
    protected generator.Abstract generator;

    public abstract T generateValue();

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public generator.Abstract getGenerator() {
        return generator;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setGenerator(generator.Abstract generator) {
        this.generator = generator;
    }
}
