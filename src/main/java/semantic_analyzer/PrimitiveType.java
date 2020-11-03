package semantic_analyzer;

public abstract class PrimitiveType extends IType {

    public PrimitiveType(String line, int row, int column) {
        super(null, line, row, column);
    }

    @Override
    public void consolidate() {

    }
}
