package exceptions;

import semantic_analyzer.Entity;
import semantic_analyzer_ast.expression_nodes.Node;

public class SemanticException extends CompilerException {
    private final Entity entity;
    private final String message;
    private final Node node;

    public SemanticException(Entity entity, String message) {
        this.entity = entity;
        this.message = message;
        node = null;
    }

    public SemanticException(Node node, String message) {
        this.node = node;
        this.message = message;
        entity = null;
    }

    public Entity getEntity() {
        return entity;
    }

    public Node getNode() {
        return node;
    }

    @Override
    public int getRow() {
        if (entity != null) {
            return entity.getRow();
        }
        return node.getRow();
    }

    @Override
    public int getColumn() {
        if (entity != null) {
            return entity.getColumn();
        }
        return node.getColumn();
    }

    @Override
    public String getRowString() {
        if (entity != null) {
            return entity.getLine();
        }
        return node.getLine();
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getLexeme() {
        if (entity != null) {
            return entity.getName();
        }
        return node.getToken().getLexeme();
    }
}
