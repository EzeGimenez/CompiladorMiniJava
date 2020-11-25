package semantic_analyzer_ast.sentence_nodes;

import exceptions.SemanticException;
import semantic_analyzer_ast.expression_nodes.ExpressionNode;
import semantic_analyzer_ast.visitors.VisitorSentence;

public class ElseNode extends SentenceNode {

    private ExpressionNode condition;
    private SentenceNode sentenceNode;
    private ElseNode elseNode;

    public ElseNode(String line, int row, int column) {
        super(line, row, column);
    }

    public SentenceNode getSentenceNode() {
        return sentenceNode;
    }

    public void setBody(SentenceNode sentenceNode) {
        this.sentenceNode = sentenceNode;
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public void setCondition(ExpressionNode condition) {
        this.condition = condition;
    }

    @Override
    public void acceptVisitor(VisitorSentence v) {
        v.visit(this);
    }

    @Override
    public void validate() throws SemanticException {

    }
}
