package semantic_analyzer;

import exceptions.SemanticException;

public abstract class IType extends Entity {

    public IType(String name, String line, int row, int column) {
        super(name, line, row, column);
    }

    public abstract void validate(IType genericType) throws SemanticException;

    public abstract void validateOverwrite(IClassType ancestorClassRef, IType ancestorType) throws SemanticException;

    public abstract IType cloneForOverwrite(String line, int row, int column);
}
