package semantic_analyzer;

public class StringType extends PrimitiveType {
    private static final String name = "String";

    public StringType() {
        super(name, "", 0, 0);
    }

    public StringType(String line, int row, int column) {
        super(name, line, row, column);
    }

    @Override
    public String getName() {
        return name;
    }

}
