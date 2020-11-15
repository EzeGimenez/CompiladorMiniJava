package semantic_analyzer;

import exceptions.SemanticException;

public abstract class IVariable extends Entity {
    public IVariable(String name, String line, int row, int column) {
        super(name, line, row, column);
    }

    abstract IType getType();

    abstract IVisibility getVisibility();

    public abstract void validate(IType genericType) throws SemanticException;

    public abstract IVariable cloneForOverwrite(IClassType parentClassRef);
}
