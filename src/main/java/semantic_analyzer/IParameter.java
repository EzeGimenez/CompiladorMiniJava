package semantic_analyzer;

import exceptions.SemanticException;

public abstract class IParameter extends Entity {

    public IParameter(String name, String line, int row, int column) {
        super(name, line, row, column);
    }

    public abstract IType getType();

    public void validateOverwrite(IClassType ancestorClassRef, IParameter ancestorParameter) throws SemanticException {
        getType().validateOverwrite(ancestorClassRef, ancestorParameter.getType());
    }

    public abstract IParameter cloneForOverWrite(String line, int row, int column);
}
