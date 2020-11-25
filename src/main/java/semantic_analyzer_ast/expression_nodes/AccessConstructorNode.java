package semantic_analyzer_ast.expression_nodes;

import exceptions.SemanticException;
import semantic_analyzer.IType;
import semantic_analyzer.SymbolTable;
import semantic_analyzer_ast.visitors.VisitorExpression;

import java.util.ArrayList;
import java.util.List;

public class AccessConstructorNode extends AccessNode {
    private final List<ExpressionNode> actualParameters;
    private GenericityNode genericityNode;

    public AccessConstructorNode(String line, int row, int column) {
        super(line, row, column);
        actualParameters = new ArrayList<>();
    }

    @Override
    public IType getType() {
        return SymbolTable.getInstance().getClass(getToken().getLexeme()).getConstructor().getReturnType();
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
        throw new SemanticException(this, "asignacion a un constructor");
    }

    @Override
    public void validate() throws SemanticException {

    }

    public GenericityNode getGenericityNode() {
        return genericityNode;
    }

    public void setGenericityNode(GenericityNode genericityNode) {
        this.genericityNode = genericityNode;
    }

    public List<ExpressionNode> getActualParameters() {
        return actualParameters;
    }


}
