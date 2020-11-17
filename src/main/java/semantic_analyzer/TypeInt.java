package semantic_analyzer;

public class TypeInt extends TypePrimitive {
    private static final String name = "int";

    public TypeInt() {
        super(name, "", 0, 0);
    }

    public TypeInt(String line, int row, int column) {
        super(name, line, row, column);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public IType cloneForOverwrite(String line, int row, int column) {
        return new TypeInt(getLine(), row, column);
    }
}
