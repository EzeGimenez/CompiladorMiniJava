package semantic_analyzer;

public abstract class IType extends Entity {

    public IType(String name, String line, int row, int column) {
        super(name, line, row, column);
    }

    public abstract void validate(IType genericType) throws SemanticException;
}
