package semantic_analyzer;

import exceptions.SemanticException;

public class Variable extends IVariable {

    private final IType type;
    private final IVisibility visibility;

    public Variable(String name, IVisibility visibility, IType type, String line, int row, int column) {
        super(name, line, row, column);
        this.type = type;
        this.visibility = visibility;
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
    public void compareTo(Object o) throws SemanticException {
        if (o == null || getClass() != o.getClass()) throw new SemanticException(this, "diferentes");
        Variable variable = (Variable) o;
        type.compareTo(variable.type);
        visibility.compareTo(variable.visibility);
    }
}
