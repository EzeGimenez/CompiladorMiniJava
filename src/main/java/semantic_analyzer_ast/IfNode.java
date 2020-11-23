package semantic_analyzer_ast;

public class IfNode extends SentenceNode {
    private ExpressionNode condition;
    private SentenceNode sentenceNode;
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

    public SentenceNode getSentenceNode() {
        return sentenceNode;
    }

    public void setBody(SentenceNode sentenceNode) {
        this.sentenceNode = sentenceNode;
    }

    @Override
    public void validate() {

    }
}
