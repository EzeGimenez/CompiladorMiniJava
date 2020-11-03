package semantic_analyzer;

import java.util.Objects;

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

    @Override
    public void compareTo(Object o) throws SemanticException {
        if (o == null || getClass() != o.getClass()) throw new SemanticException(this, "diferentes");

        if (!Objects.equals(getName(), ((ReferenceType) o).getName())) {
            throw new SemanticException(this, "nombres diferentes");
        }
    }
}
