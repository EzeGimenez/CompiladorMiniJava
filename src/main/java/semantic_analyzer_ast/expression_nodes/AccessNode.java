package semantic_analyzer_ast.expression_nodes;

public abstract class AccessNode extends OperandNode {
    public AccessNode(String line, int row, int column) {
        super(line, row, column);
    }
}
