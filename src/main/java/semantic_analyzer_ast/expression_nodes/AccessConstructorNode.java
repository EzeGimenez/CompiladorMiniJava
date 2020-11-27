package semantic_analyzer_ast.expression_nodes;

import exceptions.SemanticException;
import semantic_analyzer.*;
import semantic_analyzer_ast.visitors.VisitorExpression;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AccessConstructorNode extends AccessNode {
    private final List<ExpressionNode> actualParameters;
    private GenericityNode genericityNode;

    public AccessConstructorNode(String line, int row, int column) {
        super(line, row, column);
        actualParameters = new ArrayList<>();
    }

    @Override
    public IType getCurrentType() throws SemanticException {
        return getConstructor().getReturnType();
    }

    @Override
    public IType getType() throws SemanticException {
        validate();
        if (getChainedNode() == null) {
            return getCurrentType();
        } else {
            return getChainedNode().getType(getCurrentType());
        }
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
    public void validate() throws SemanticException {
        IType type = getCurrentType();
        validateParameters(getConstructor());
        if (getChainedNode() != null) {
            getChainedNode().validate(type);
        }
    }


    private void validateParameters(IMethod method) throws SemanticException {
        if (actualParameters.size() != method.getParameterList().size()) {
            throw new SemanticException(this, "distinto numero de parametros");
        }

        Iterator<ExpressionNode> expressionNodeIterator = actualParameters.iterator();
        Iterator<IParameter> parameterIterator = method.getParameterList().iterator();

        ExpressionNode expressionNode;
        IType actualType, parameterType;
        while (expressionNodeIterator.hasNext() && parameterIterator.hasNext()) {
            expressionNode = expressionNodeIterator.next();
            actualType = expressionNode.getType();
            parameterType = parameterIterator.next().getType();

            if (!actualType.acceptTypeChecker(parameterType.getTypeChecker())) {
                throw new SemanticException(expressionNode, "tipo de parametro diferente");
            }
        }
    }

    private IMethod getConstructor() throws SemanticException {
        IClass iClass = SymbolTable.getInstance().getClass(getToken().getLexeme());
        if (iClass == null) {
            throw new SemanticException(this, "clase no definida");
        }

        return iClass.getConstructor();
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
