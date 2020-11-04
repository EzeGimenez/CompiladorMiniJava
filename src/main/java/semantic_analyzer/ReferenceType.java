package semantic_analyzer;

import java.util.Objects;

public class ReferenceType extends IType {

    private final IClassReference referencedClass;

    public ReferenceType(IClassReference referencedClass, String line, int row, int column) {
        super(referencedClass.getName(), line, row, column);
        this.referencedClass = referencedClass;
    }

    public ReferenceType(IClassReference classReference) {
        this(classReference, "", 0, 0);
    }

    @Override
    public void compareTo(Object o) throws SemanticException {
        if (o == null || getClass() != o.getClass()) throw new SemanticException(this, "diferentes");

        if (!Objects.equals(getName(), ((ReferenceType) o).getName())) {
            throw new SemanticException(this, "nombres diferentes");
        }
    }

    public IClassReference getReferencedClass() {
        return referencedClass;
    }

    @Override
    public void validate(IClassReference genericType) throws SemanticException {
        referencedClass.validate(genericType);
    }

    private boolean existsClass(IType genericType) {
        return SymbolTable.getInstance().containsClass(genericType.getName());
    }
}
