package semantic_analyzer;

// TODO design concern
public abstract class IClassReference extends Entity {

    public IClassReference(String name, String line, int row, int column) {
        super(name, line, row, column);
    }

    public abstract IClassReference getGenericClass();

    public abstract void setGenericClass(IClassReference genericClass);

    public abstract void validate(IClassReference genericClass) throws SemanticException;
}
