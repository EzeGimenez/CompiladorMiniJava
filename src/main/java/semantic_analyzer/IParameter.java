package semantic_analyzer;

public abstract class IParameter extends Entity {

    public IParameter(String name, String line, int row, int column) {
        super(name, line, row, column);
    }

    abstract IType getType();
}
