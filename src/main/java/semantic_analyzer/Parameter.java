package semantic_analyzer;

public class Parameter implements IParameter {

    private final String name;
    private final IType type;

    public Parameter(String name, IType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public IType getType() {
        return type;
    }
}
