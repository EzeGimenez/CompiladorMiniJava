package semantic_analyzer;

import exceptions.SemanticException;

import java.util.Objects;

public class ClassType extends IClassType {
    private IClassType genericType;

    public ClassType(String name, String line, int row, int column) {
        super(name, line, row, column);
    }

    public ClassType(String name) {
        this(name, "", 0, 0);
    }

    @Override
    public IClassType getGenericType() {
        return genericType;
    }

    @Override
    public void setGenericType(IClassType genericType) {
        this.genericType = genericType;
    }

    @Override
    public void validate(IType genericTypeFromHolderClass) throws SemanticException {
        IClass referencedClass = SymbolTable.getInstance().getClass(getName());
        IInterface referencedInterface = SymbolTable.getInstance().getInterface(getName());

        if (referencedClass == null &&
                referencedInterface == null &&
                !equalsToGenericType(genericTypeFromHolderClass)) {
            throw new SemanticException(this, getName() + " no esta definido");
        }

        if (genericType != null && referencedClass != null && referencedClass.getGenericType() == null) {
            throw new SemanticException(this, "la clase no es generica");
        }

        if (genericType != null && referencedInterface != null && referencedInterface.getGenericType() == null) {
            throw new SemanticException(this, "la clase no es generica");
        }

        if (genericType == null) {
            if (referencedClass != null && referencedClass.getGenericType() != null) {
                throw new SemanticException(this, "Falta el tipo parametrico de clase generica " + getName());
            }
            if (referencedInterface != null && referencedInterface.getGenericType() != null) {
                throw new SemanticException(this, "Falta el tipo parametrico de interfaz generica " + getName());
            }

        } else {
            genericType.validate(genericTypeFromHolderClass);
        }
    }

    @Override
    public void validateOverwrite(IClassType ancestorRef, IType ancestorType) throws SemanticException {
        validate(ancestorRef.getGenericType());
        if (ancestorType.getClass() != getClass()) {
            throw new SemanticException(this, "distinto tipo");
        }

        IType ancestorGenericType = getAncestorClassGenericType(ancestorRef);
        if (Objects.equals(ancestorGenericType, ancestorType)) {
            if (!equals(ancestorRef.getGenericType())) {
                throw new SemanticException(this, "distinto tipo");
            }
        } else {
            if (!equals(ancestorType)) {
                throw new SemanticException(this, "distinto tipo");
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassType classType = (ClassType) o;
        if (!Objects.equals(getName(), classType.getName())) return false;
        return Objects.equals(genericType, classType.genericType);
    }

    private IType getAncestorClassGenericType(IClassType ancestorRef) {
        IClass ancestorClass = SymbolTable.getInstance().getClass(ancestorRef.getName());
        if (ancestorClass != null) {
            return ancestorClass.getGenericType();
        }
        IInterface iInterface = SymbolTable.getInstance().getInterface(ancestorRef.getName());
        return iInterface.getGenericType();
    }

    @Override
    public IType cloneForOverwrite(String line, int row, int column) {
        return new ClassType(getName(), line, row, column);
    }

    private boolean equalsToGenericType(IType holderClassGenericType) {
        return holderClassGenericType != null &&
                Objects.equals(getName(), holderClassGenericType.getName());
    }

    @Override
    public void compareTo(Object o) throws SemanticException {
        if (o == null || getClass() != o.getClass()) {
            throw new SemanticException(this, "tipos no iguales");
        }
        ClassType classRef = (ClassType) o;
        if (!Objects.equals(this.getName(), classRef.getName())) {
            throw new SemanticException(this, "nombre diferente");
        }
        if (genericType != null) {
            genericType.compareTo(classRef.genericType);
        }
    }

}
