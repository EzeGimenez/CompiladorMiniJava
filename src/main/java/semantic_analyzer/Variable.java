package semantic_analyzer;

import exceptions.SemanticException;

public class Variable extends IVariable {

    private final IType type;
    private final IVisibility visibility;
    private final IAccessMode accessMode;

    public Variable(String name, IAccessMode accessMode, IVisibility visibility, IType type, String line, int row, int column) {
        super(name, line, row, column);
        this.type = type;
        this.accessMode = accessMode;
        this.visibility = visibility;
    }

    @Override
    public IAccessMode getAccessMode() {
        return accessMode;
    }

    @Override
    public IType getType() {
        return type;
    }

    @Override
    public IVisibility getVisibility() {
        return visibility;
    }

    @Override
    public void validate(IType genericType) throws SemanticException {
        type.validate(genericType);
    }

    @Override
    public IVariable cloneForOverwrite(IClassType parentClassRef) {
        IClass parentClass = SymbolTable.getInstance().getClass(parentClassRef.getName());
        IType outType = getType().cloneForOverwrite(getType().getLine(), getType().getRow(), getType().getColumn());
        if (getType().equals(parentClass.getGenericType())) {
            outType = parentClassRef.getGenericType();
        }
        return new Variable(getName(), accessMode, getVisibility(), outType, getLine(), getRow(), getColumn());
    }

    @Override
    public void compareTo(Object o) throws SemanticException {
        if (o == null || getClass() != o.getClass()) throw new SemanticException(this, "diferentes");
        Variable variable = (Variable) o;
        type.compareTo(variable.type);
        visibility.compareTo(variable.visibility);
    }
}
