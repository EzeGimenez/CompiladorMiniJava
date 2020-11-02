package semantic_analyzer;

public class ReferenceType implements IType {

    private final String name;
    private final String genericClass;

    public ReferenceType(String name, String genericClass) {
        this.name = name;
        this.genericClass = genericClass;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getGenericClass() {
        return genericClass;
    }
}
