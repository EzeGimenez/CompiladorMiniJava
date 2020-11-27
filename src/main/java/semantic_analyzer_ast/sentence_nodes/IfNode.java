package semantic_analyzer_ast.sentence_nodes;

import exceptions.SemanticException;
import semantic_analyzer_ast.expression_nodes.ExpressionNode;
import semantic_analyzer_ast.visitors.VisitorEndsInReturn;
import semantic_analyzer_ast.visitors.VisitorSentence;

public class IfNode extends SentenceNode {
    private ExpressionNode condition;
    private SentenceNode body;
    private ElseNode elseNode;

    public IfNode(String line, int row, int column) {
        super(line, row, column);
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public void setCondition(ExpressionNode condition) {
        this.condition = condition;
    }

    public ElseNode getElseNode() {
        return elseNode;
    }

    public void setElseNode(ElseNode elseNode) {
        this.elseNode = elseNode;
    }

    public SentenceNode getBody() {
        return body;
    }

    public void setBody(SentenceNode sentenceNode) {
        this.body = sentenceNode;
    }

    @Override
    public void acceptVisitor(VisitorSentence v) {
        v.visit(this);
    }

    @Override
    public void validate() throws SemanticException {
        condition.validate();
        if (!condition.getType().getName().equals("boolean")) {
            throw new SemanticException(this, "debe ser una expresion con tipo boolean");
        }
        body.validate();
        if (elseNode != null) {
            elseNode.validate();
        }

    }

    public boolean endsInReturn() {
        VisitorEndsInReturn visitorEndsInReturn = new VisitorEndsInReturn();
        body.acceptVisitor(visitorEndsInReturn);
        if (visitorEndsInReturn.endsInReturn()) {
            if (elseNode != null) {
                return elseNode.endsInReturn();
            }
        }
        return false;
    }
}
