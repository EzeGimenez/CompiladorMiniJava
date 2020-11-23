package semantic_analyzer_ast;

public class AssignmentNode extends SentenceNode {

    private ExpressionNode leftSide;
    private ExpressionNode rightSide;

    public AssignmentNode(String line, int row, int column) {
        super(line, row, column);
    }

    @Override
    public void validate() {

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
}