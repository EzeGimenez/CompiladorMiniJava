package semantic_analyzer;

public class TypeChar extends TypePrimitive {

    private static final String name = "char";

    public TypeChar() {
        this("", 0, 0);
    }

    public TypeChar(String line, int row, int column) {
        super(name, line, row, column);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public IType cloneForOverwrite(String line, int row, int column) {
        return new TypeChar(line, row, column);
    }
}
