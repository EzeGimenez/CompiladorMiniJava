package semantic_analyzer_ast.expression_nodes;

import exceptions.SemanticException;
import semantic_analyzer.*;
import semantic_analyzer_ast.visitors.VisitorExpression;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AccessMethodNode extends AccessNode {
    private final List<ExpressionNode> actualParameters;

    public AccessMethodNode(String line, int row, int column) {
        super(line, row, column);
        actualParameters = new ArrayList<>();
    }

    @Override
    public IType getType() throws SemanticException {
        if (getChainedNode() != null) {
            return getChainedNode().getType(getCurrentType());
        }
        return getCurrentType();
    }

    @Override
    public void validate() throws SemanticException {
        IType type = getCurrentType();
        IMethod method = getMethod();
        if (!isInConstructor()
                && isAStaticContext()
                && method.getAccessMode().getName().equals("dynamic")) {

            throw new SemanticException(this, "acceso a metodo dinamico desde contexto estatico");
        }
        validateParameters(getMethod());
        if (getChainedNode() != null) {
            getChainedNode().validate(type);
        }
    }

    private boolean isInConstructor() {
        return SymbolTable.getInstance().getCurrMethod() == SymbolTable.getInstance().getCurrClass().getConstructor();
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

    private IMethod getMethod() throws SemanticException {
        IClass classType = SymbolTable.getInstance().getCurrClass();
        if (classType != null) {
            if (!classType.containsMethod(getToken().getLexeme())) {
                throw new SemanticException(this, "no se encontro un metodo con nombre " + getToken().getLexeme());
            } else {
                IMethod out = classType.getMethodMap().get(getToken().getLexeme());
                if (out == null) out = classType.getInheritedMethodMap().get((getToken().getLexeme()));
                return out;
            }
        } else {
            IInterface iInterface = SymbolTable.getInstance().getInterface(getToken().getLexeme());
            if (iInterface != null) {
                if (!iInterface.containsMethod(getToken().getLexeme())) {
                    throw new SemanticException(this, "no se encontro un metodo con nombre " + getToken().getLexeme());
                } else {
                    return iInterface.getMethodMap().get(getToken().getLexeme());
                }
            }
            throw new SemanticException(this, "no se encontró la clase/interfaz, o es un acceso invalido");
        }
    }

    private boolean isAStaticContext() {
        IAccessMode accessMode = SymbolTable.getInstance().getCurrMethod().getAccessMode();
        return accessMode.getName().equals("static");
    }

    @Override
    public void acceptVisitor(VisitorExpression visitorExpression) {
        if (getChainedNode() != null) {
            getChainedNode().acceptVisitor(visitorExpression);
        } else {
            visitorExpression.visit(this);
        }
    }

    public List<ExpressionNode> getActualParameters() {
        return actualParameters;
    }

    @Override
    public IType getCurrentType() throws SemanticException {
        return getMethod().getReturnType();
    }

}
