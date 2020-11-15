package semantic_analyzer;

import exceptions.SemanticException;

public abstract class IClassType extends IType {

    public IClassType(String name, String line, int row, int column) {
        super(name, line, row, column);
    }

    public abstract IClassType getGenericType();

    public abstract void setGenericType(IClassType genericType);

    public abstract void validateOverwrite(IClassType ancestorClassRef, IType ancestorType) throws SemanticException;

    public abstract boolean equals(Object obj);
}
