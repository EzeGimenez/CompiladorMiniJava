package semantic_analyzer_ast.expression_nodes;

import exceptions.SemanticException;
import semantic_analyzer_ast.visitors.VisitorExpression;

public abstract class TypeNode extends OperandNode {
    public TypeNode(String line, int row, int column) {
        super(line, row, column);
    }

    @Override
    public void validate() throws SemanticException {

    }

    @Override
    public void validateForAssignment() throws SemanticException {
        throw new SemanticException(this, "no se le puede asignar algo a un tipo");
    }

    @Override
    public void acceptVisitor(VisitorExpression visitorExpression) {
        visitorExpression.visit(this);
    }
}
