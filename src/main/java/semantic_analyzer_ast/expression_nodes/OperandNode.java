package semantic_analyzer_ast.expression_nodes;

public abstract class OperandNode extends ExpressionNode {
    public OperandNode(String line, int row, int column) {
        super(line, row, column);
    }

}
