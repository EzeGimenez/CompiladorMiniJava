package semantic_analyzer_ast;

public class ReturnNode extends SentenceNode {
    private ExpressionNode expressionNode;

    public ReturnNode(String line, int row, int column) {
        super(line, row, column);
    }

    public ExpressionNode getExpressionNode() {
        return expressionNode;
    }

    public void setExpressionNode(ExpressionNode expressionNode) {
        this.expressionNode = expressionNode;
    }

    @Override
    public void validate() {

    }
}
