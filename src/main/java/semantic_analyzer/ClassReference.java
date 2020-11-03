package semantic_analyzer;

public class ClassReference extends IClassReference {
    private IClassReference genericClass;

    public ClassReference(String name, String line, int row, int column) {
        super(name, line, row, column);
    }

    @Override
    public IClassReference getGenericClass() {
        return genericClass;
    }

    @Override
    public void setGenericClass(IClassReference genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    public IClassReference getDeepestMismatchClassRef(IClassReference genericClass) {
        if (this.getName().equals(genericClass.getName())) {
            return null;
        }
        if (this.getGenericClass() == null) {
            return this;
        }
        return this.getGenericClass().getDeepestMismatchClassRef(genericClass);
    }

    @Override
    public void consolidate() throws SemanticException {

    }
}
