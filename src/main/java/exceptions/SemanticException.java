package exceptions;

import semantic_analyzer.Entity;

public class SemanticException extends CompilerException {
    private final Entity entity;
    private final String message;

    public SemanticException(Entity entity, String message) {
        this.entity = entity;
        this.message = message;
    }

    public Entity getEntity() {
        return entity;
    }

    @Override
    public int getRow() {
        return entity.getRow();
    }

    @Override
    public int getColumn() {
        return entity.getColumn();
    }

    @Override
    public String getRowString() {
        return entity.getLine();
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getLexeme() {
        return entity.getName();
    }
}
