package semantic_analyzer_ast.expression_nodes;

import exceptions.SemanticException;
import semantic_analyzer.IType;
import semantic_analyzer_ast.visitors.VisitorExpression;

public class AccessStaticNode extends AccessNode {

    private IType classType;

    public AccessStaticNode(String line, int row, int column) {
        super(line, row, column);
    }

    @Override
    public IType getType() throws SemanticException {
        if (getChainedNode() != null) {
            return getChainedNode().getType(classType);
        }
        return classType;
    }

    @Override
    public void acceptVisitor(VisitorExpression visitorExpression) {
        if (getChainedNode() != null) {
            getChainedNode().acceptVisitor(visitorExpression);
        } else {
            visitorExpression.visit(this);
        }
    }

    @Override
    public void validateForAssignment() throws SemanticException {
        if (getChainedNode() == null) {
            throw new SemanticException(this, "asignacion a acceso estatico");
        }
        getChainedNode().validateForAssignemnt(getType());
    }

    public IType getClassType() {
        return classType;
    }

    public void setClassType(IType classType) {
        this.classType = classType;
    }

    @Override
    public void validate() throws SemanticException {

    }

}
