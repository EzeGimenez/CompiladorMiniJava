package semantic_analyzer;

public class SemanticException extends Exception {
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
    public String getMessage() {
        return message;
    }
}
