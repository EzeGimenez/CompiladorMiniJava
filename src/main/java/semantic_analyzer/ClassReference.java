package semantic_analyzer;

public class ClassReference implements IClassReference {

    private final String name, genericClass;

    public ClassReference(String name, String genericClass) {
        this.name = name;
        this.genericClass = genericClass;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getGenericClass() {
        return genericClass;
    }
}
