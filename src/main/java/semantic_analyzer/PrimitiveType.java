package semantic_analyzer;

public class PrimitiveType implements IType {
    private final String name;

    public PrimitiveType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

}
