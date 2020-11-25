package semantic_analyzer_ast.expression_nodes;

import semantic_analyzer.IType;
import semantic_analyzer_ast.visitors.VisitorExpression;

public class ExpressionBinaryIntegerNode extends ExpressionBinaryNode {
    public ExpressionBinaryIntegerNode(String line, int row, int column) {
        super(line, row, column);
    }

    @Override
    public IType getType() {
        return null;
    }

    @Override
    public void acceptVisitor(VisitorExpression visitorExpression) {
        visitorExpression.visit(this);
    }
}
