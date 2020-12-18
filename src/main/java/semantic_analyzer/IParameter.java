package semantic_analyzer;

import exceptions.SemanticException;

public abstract class IParameter extends Entity {

    private int offset;

    public IParameter(String name, String line, int row, int column) {
        super(name, line, row, column);
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public abstract IType getType();

    public void validateOverwrite(IClassType ancestorClassRef, IParameter ancestorParameter) throws SemanticException {
        getType().validateOverwrite(ancestorClassRef, ancestorParameter.getType());
    }

    public abstract IParameter cloneForOverWrite(String line, int row, int column);
}
