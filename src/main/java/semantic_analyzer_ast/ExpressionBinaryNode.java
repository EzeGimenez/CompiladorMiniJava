package semantic_analyzer_ast;

public class ExpressionBinaryNode extends ExpressionNode {

    private ExpressionNode leftSide, rightSide;

    public ExpressionBinaryNode(String line, int row, int column) {
        super(line, row, column);
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
