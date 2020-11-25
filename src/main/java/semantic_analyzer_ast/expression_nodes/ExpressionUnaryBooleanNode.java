package semantic_analyzer_ast.expression_nodes;

import semantic_analyzer.IType;

public class ExpressionUnaryBooleanNode extends ExpressionUnaryNode {
    public ExpressionUnaryBooleanNode(String line, int row, int column) {
        super(line, row, column);
    }

    @Override
    public IType getType() {
        return null;
    }
}
