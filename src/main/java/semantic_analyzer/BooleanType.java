package semantic_analyzer;

public class BooleanType extends PrimitiveType {
    private static final String name = "boolean";

    public BooleanType() {
        super(name, "", 0, 0);
    }

    public BooleanType(String line, int row, int column) {
        super(name, line, row, column);
    }

    @Override
    public String getName() {
        return "boolean";
    }
}
