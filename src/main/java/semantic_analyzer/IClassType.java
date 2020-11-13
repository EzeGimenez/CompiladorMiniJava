package semantic_analyzer;

public abstract class IClassType extends IType {

    public IClassType(String name, String line, int row, int column) {
        super(name, line, row, column);
    }

    public abstract IClassType getGenericType();

    public abstract void setGenericType(IClassType genericType);
}
