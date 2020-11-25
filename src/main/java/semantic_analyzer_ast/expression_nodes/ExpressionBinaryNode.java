package semantic_analyzer_ast.expression_nodes;

import exceptions.SemanticException;

public abstract class ExpressionBinaryNode extends ExpressionNode {

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
    public void validate() throws SemanticException {

    }

    @Override
    public void validateForAssignment() throws SemanticException {
        throw new SemanticException(this, "no se le puede asignar algo a una expresion binaria");
    }
}
