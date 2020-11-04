package semantic_analyzer;

public class IntType extends PrimitiveType {
    private static final String name = "int";

    public IntType() {
        super(name, "", 0, 0);
    }

    public IntType(String line, int row, int column) {
        super(name, line, row, column);
    }

    @Override
    public String getName() {
        return name;
    }
}
