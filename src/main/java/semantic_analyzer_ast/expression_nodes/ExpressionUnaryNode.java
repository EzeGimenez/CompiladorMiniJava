package semantic_analyzer_ast.expression_nodes;

import semantic_analyzer_ast.visitors.VisitorExpression;

public abstract class ExpressionUnaryNode extends ExpressionNode {
    private ExpressionNode operandNode;

    public ExpressionUnaryNode(String line, int row, int column) {
        super(line, row, column);
    }

    public ExpressionNode getOperandNode() {
        return operandNode;
    }

    public void setOperandNode(ExpressionNode operandNode) {
        this.operandNode = operandNode;
    }

    @Override
    public void acceptVisitor(VisitorExpression visitorExpression) {
        if (getChainedNode() != null) {
            getChainedNode().acceptVisitor(visitorExpression);
        } else {
            visitorExpression.visit(this);
        }
    }
}
