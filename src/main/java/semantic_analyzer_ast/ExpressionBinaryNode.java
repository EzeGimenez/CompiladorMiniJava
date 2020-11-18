package semantic_analyzer_ast;

import lexical_analyzer.IToken;

public class ExpressionBinaryNode extends ExpressionNode {

    private ExpressionNode leftSide, rightSide;

    public ExpressionBinaryNode(IToken token, String line, int row, int column) {
        super(token, line, row, column);
    }

    public ExpressionNode getLeftSide() {
        return leftSide;
    }

    public void setLeftSide(ExpressionNode leftSide) {
        this.leftSide = leftSide;
    }

    public ExpressionNode getRightSide() {
        return rightSide;
    }

    public void setRightSide(ExpressionNode rightSide) {
        this.rightSide = rightSide;
    }

    @Override
    public void validate() {

    }

}
