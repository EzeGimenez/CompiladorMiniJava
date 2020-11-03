package semantic_analyzer;

public class Parameter extends IParameter {

    private final IType type;

    public Parameter(String name, IType type, String line, int row, int column) {
        super(name, line, row, column);
        this.type = type;
    }

    @Override
    public IType getType() {
        return type;
    }

    @Override
    public void consolidate() throws SemanticException {

    }
}
