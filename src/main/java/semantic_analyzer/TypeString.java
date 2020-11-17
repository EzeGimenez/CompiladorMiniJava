package semantic_analyzer;

public class TypeString extends TypePrimitive {
    private static final String name = "String";

    public TypeString() {
        super(name, "", 0, 0);
    }

    public TypeString(String line, int row, int column) {
        super(name, line, row, column);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public IType cloneForOverwrite(String line, int row, int column) {
        return new TypeString(line, row, column);
    }
}
