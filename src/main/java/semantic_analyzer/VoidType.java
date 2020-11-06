package semantic_analyzer;

import exceptions.SemanticException;

import java.util.Objects;

public class VoidType extends IType {

    public VoidType() {
        this("", 0, 0);
    }

    public VoidType(String line, int row, int column) {
        super("void", line, row, column);
    }

    @Override
    public void validate(IType genericType) {

    }

    @Override
    public void compareTo(Object o) throws SemanticException {
        if (o == null || getClass() != o.getClass()) throw new SemanticException(this, "diferente tipo");
        if (!Objects.equals(this.getName(), ((VoidType) o).getName())) {
            throw new SemanticException(this, "tipos diferentes");
        }
    }

    @Override
    public void validateOverwrite(IClassType ancestorClassRef, IType ancestorType) throws SemanticException {
        if (ancestorType == null || getClass() != ancestorType.getClass()) {
            throw new SemanticException(this, "distinto tipo");
        }
    }

    @Override
    public IType cloneForOverwrite(String line, int row, int column) {
        return new VoidType(line, row, column);
    }
}
