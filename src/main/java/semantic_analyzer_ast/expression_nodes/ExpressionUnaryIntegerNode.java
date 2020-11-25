package semantic_analyzer_ast.expression_nodes;

import semantic_analyzer.IType;

public class ExpressionUnaryIntegerNode extends ExpressionUnaryNode {
    public ExpressionUnaryIntegerNode(String line, int row, int column) {
        super(line, row, column);
    }

    @Override
    public IType getType() {
        return null;
    }
}
