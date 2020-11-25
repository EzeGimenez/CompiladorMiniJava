package semantic_analyzer_ast.sentence_nodes;

import exceptions.SemanticException;
import semantic_analyzer_ast.expression_nodes.ExpressionNode;
import semantic_analyzer_ast.visitors.VisitorIsBoolean;
import semantic_analyzer_ast.visitors.VisitorSentence;

public class WhileNode extends SentenceNode {
    private ExpressionNode condition;
    private SentenceNode body;

    public WhileNode(String line, int row, int column) {
        super(line, row, column);
    }

    public SentenceNode getBody() {
        return body;
    }

    public void setBody(SentenceNode body) {
        this.body = body;
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public void setCondition(ExpressionNode condition) {
        this.condition = condition;
    }

    @Override
    public void validate() throws SemanticException {
        condition.validate();
        VisitorIsBoolean visitorIsBoolean = new VisitorIsBoolean();
        condition.acceptVisitor(visitorIsBoolean);
        if (!visitorIsBoolean.isBoolean()) {
            throw new SemanticException(condition, "debe ser una expresion con tipo boolean");
        }
        body.validate();
    }

    @Override
    public void acceptVisitor(VisitorSentence v) {
        v.visit(this);
    }
}
