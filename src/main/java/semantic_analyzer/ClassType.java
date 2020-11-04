package semantic_analyzer;

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

        if (genericType == null) { //TODO check if its a class or interface see error on herenciaError.java
            if (referencedClass == null &&
                    referencedInterface == null &&
                    !equalsToGenericType(genericTypeFromHolderClass)) {
                throw new SemanticException(this, getName() + " no esta definido");

            } else if (referencedClass != null && referencedClass.getGenericType() != null) {
                throw new SemanticException(this, "Falta el tipo parametrico de clase generica " + getName());

            } else if (referencedInterface != null && referencedInterface.getGenericType() != null) {
                throw new SemanticException(this, "Falta el tipo parametrico de interfaz generica " + getName());
            }
        } else {
            genericType.validate(genericTypeFromHolderClass);
        }
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
