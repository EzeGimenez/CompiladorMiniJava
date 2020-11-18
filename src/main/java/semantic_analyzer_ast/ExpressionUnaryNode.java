package semantic_analyzer_ast;

import lexical_analyzer.IToken;

public class ExpressionUnaryNode extends ExpressionNode {
    private OperandNode operandNode;

    public ExpressionUnaryNode(IToken token, String line, int row, int column) {
        super(token, line, row, column);
    }

    public OperandNode getOperandNode() {
        return operandNode;
    }

    public void setOperandNode(OperandNode operandNode) {
        this.operandNode = operandNode;
    }

    @Override
    public void validate() {

    }
}
