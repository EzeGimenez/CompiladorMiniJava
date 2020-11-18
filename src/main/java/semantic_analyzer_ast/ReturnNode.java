package semantic_analyzer_ast;

import lexical_analyzer.IToken;

public class ReturnNode extends SentenceNode {
    private ExpressionNode expressionNode;

    public ReturnNode(IToken token, String line, int row, int column) {
        super(token, line, row, column);
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
