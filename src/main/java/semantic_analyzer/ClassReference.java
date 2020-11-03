package semantic_analyzer;

import java.util.Objects;

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

    @Override
    public void compareTo(Object o) throws SemanticException {
        if (o == null || getClass() != o.getClass()) {
            throw new SemanticException(this, "Referencia a clases no iguales");
        }
        ClassReference classRef = (ClassReference) o;
        if (!Objects.equals(this.getName(), classRef.getName())) {
            throw new SemanticException(this, "nombre diferente");
        }
        if (genericClass != null) {
            genericClass.compareTo(classRef.genericClass);
        }
    }

}
