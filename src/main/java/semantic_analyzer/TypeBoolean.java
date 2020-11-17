package semantic_analyzer;

public class TypeBoolean extends TypePrimitive {
    private static final String name = "boolean";

    public TypeBoolean() {
        super(name, "", 0, 0);
    }

    public TypeBoolean(String line, int row, int column) {
        super(name, line, row, column);
    }

    @Override
    public String getName() {
        return "boolean";
    }

    @Override
    public IType cloneForOverwrite(String line, int row, int column) {
        return new TypeBoolean(line, row, column);
    }
}
