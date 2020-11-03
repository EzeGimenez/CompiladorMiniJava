package semantic_analyzer;

public class IntType extends PrimitiveType {
    public IntType(String line, int row, int column) {
        super(line, row, column);
    }

    @Override
    public String getName() {
        return "int";
    }
}
