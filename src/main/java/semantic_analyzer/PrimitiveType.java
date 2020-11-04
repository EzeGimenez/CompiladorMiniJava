package semantic_analyzer;

import java.util.Objects;

public abstract class PrimitiveType extends IType {

    public PrimitiveType(String name) {
        super(name, "", 0, 0);
    }

    public PrimitiveType(String name, String line, int row, int column) {
        super(name, line, row, column);
    }

    @Override
    public void validate(IType genericType) {

    }

    @Override
    public void compareTo(Object o) throws SemanticException {
        if (o == null || getClass() != o.getClass()) throw new SemanticException(this, "diferente tipo");
        if (!Objects.equals(this.getName(), ((PrimitiveType) o).getName())) {
            throw new SemanticException(this, "nombres diferentes");
        }
    }
}
