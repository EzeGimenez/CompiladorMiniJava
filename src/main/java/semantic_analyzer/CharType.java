package semantic_analyzer;

public class CharType extends PrimitiveType {

    private static final String name = "char";

    public CharType() {
        this("", 0, 0);
    }

    public CharType(String line, int row, int column) {
        super(name, line, row, column);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public IType cloneForOverwrite(String line, int row, int column) {
        return new CharType(line, row, column);
    }
}
