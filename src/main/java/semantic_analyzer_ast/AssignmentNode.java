package semantic_analyzer_ast;

import lexical_analyzer.IToken;

public class AssignmentNode extends SentenceNode {

    private VariableNode leftSide;
    private ExpressionNode rightSide;

    public AssignmentNode(IToken token, String line, int row, int column) {
        super(token, line, row, column);
    }

    @Override
    public void validate() {

    }

    public VariableNode getLeftSide() {
        return leftSide;
    }

    public void setLeftSide(VariableNode leftSide) {
        this.leftSide = leftSide;
    }

    public ExpressionNode getRightSide() {
        return rightSide;
    }

    public void setRightSide(ExpressionNode rightSide) {
        this.rightSide = rightSide;
    }
}
