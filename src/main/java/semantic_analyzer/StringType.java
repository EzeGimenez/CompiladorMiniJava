package semantic_analyzer;

public class StringType extends PrimitiveType {
    public StringType(String line, int row, int column) {
        super(line, row, column);
    }

    @Override
    public String getName() {
        return "String";
    }

}
