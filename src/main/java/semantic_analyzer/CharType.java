package semantic_analyzer;

public class CharType extends PrimitiveType {
    public CharType(String line, int row, int column) {
        super(line, row, column);
    }

    @Override
    public String getName() {
        return "char";
    }
}
