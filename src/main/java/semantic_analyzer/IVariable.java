package semantic_analyzer;

public abstract class IVariable extends Entity {
    public IVariable(String name, String line, int row, int column) {
        super(name, line, row, column);
    }

    abstract IType getType();

    abstract IVisibility getVisibility();

    public abstract void validate(IType genericType) throws SemanticException;
}
