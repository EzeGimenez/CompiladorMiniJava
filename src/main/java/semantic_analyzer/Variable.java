package semantic_analyzer;

public class Variable implements IVariable {

    private final String name;
    private final IType type;
    private IVisibility visibility;

    public Variable(IVisibility visibility, String name, IType type) {
        this.name = name;
        this.type = type;
        this.visibility = visibility;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public IType getType() {
        return type;
    }

    @Override
    public IVisibility getVisibility() {
        return visibility;
    }
}
