package semantic_analyzer;

public class ReferenceType extends IType {

    private final IClassReference genericClass;

    public ReferenceType(String name, IClassReference genericClass, String line, int row, int column) {
        super(name, line, row, column);
        this.genericClass = genericClass;
    }

    public IClassReference getGenericClass() {
        return genericClass;
    }

    @Override
    public void consolidate() throws SemanticException {

    }
}
