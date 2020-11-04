package semantic_analyzer;

import java.util.Objects;

public class ClassReference extends IClassReference {
    private IClassReference genericClass;

    public ClassReference(String name, String line, int row, int column) {
        super(name, line, row, column);
    }

    public ClassReference(String name) {
        this(name, "", 0, 0);
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
    public void validate(IClassReference generic) throws SemanticException {
        if (genericClass == null) {
            IClass referencedClass = SymbolTable.getInstance().getClass(getName());
            if (referencedClass != null && referencedClass.getGenericType() != null) {
                throw new SemanticException(this, "Falta el tipo parametrico de clase generica " + getName());
            }
            IInterface referencedInterface = SymbolTable.getInstance().getInterface(getName());
            if (referencedInterface != null && referencedInterface.getGenericType() != null) {
                throw new SemanticException(this, "Falta el tipo parametrico de interfaz generica " + getName());
            }
            if (!getName().equals(generic.getName())) {
                throw new SemanticException(this, getName() + " no definido");
            }
        }

        if (genericClass != null) {
            genericClass.validate(generic);
        }
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
