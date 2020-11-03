package semantic_analyzer;

public class BooleanType extends PrimitiveType {
    public BooleanType(String line, int row, int column) {
        super(line, row, column);
    }

    @Override
    public String getName() {
        return "boolean";
    }
}
