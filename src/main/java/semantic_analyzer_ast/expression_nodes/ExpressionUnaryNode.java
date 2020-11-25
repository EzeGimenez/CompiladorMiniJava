package semantic_analyzer_ast.expression_nodes;

import exceptions.SemanticException;

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
    public void validate() throws SemanticException {

    }
}
