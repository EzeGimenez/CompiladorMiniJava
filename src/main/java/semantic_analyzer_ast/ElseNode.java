package semantic_analyzer_ast;

public class ElseNode extends SentenceNode {

    private ExpressionNode condition;
    private SentenceNode sentenceNode;
    private ElseNode elseNode;

    public SentenceNode getSentenceNode() {
        return sentenceNode;
    }

    public ElseNode(String line, int row, int column) {
        super(line, row, column);
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
    public void validate() {

    }
}
