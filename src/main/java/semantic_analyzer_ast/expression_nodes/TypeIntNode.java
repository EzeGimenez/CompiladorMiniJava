package semantic_analyzer_ast.expression_nodes;

import semantic_analyzer.IType;
import semantic_analyzer.TypeInt;
import semantic_analyzer_ast.visitors.VisitorExpression;

public class TypeIntNode extends TypeNode {
    public TypeIntNode(String line, int row, int column) {
        super(line, row, column);
    }

    @Override
    public IType getType() {
        return new TypeInt(getLine(), getRow(), getColumn());
    }

    @Override
    public void acceptVisitor(VisitorExpression visitorExpression) {
        visitorExpression.visit(this);
    }
}
