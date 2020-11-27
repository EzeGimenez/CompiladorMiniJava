package semantic_analyzer_ast.expression_nodes;

import exceptions.SemanticException;
import semantic_analyzer.*;
import semantic_analyzer_ast.visitors.VisitorExpression;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChainedMethodNode extends ChainedNode {
    private final List<ExpressionNode> actualParameters;

    public ChainedMethodNode(String line, int row, int column) {
        super(line, row, column);
        actualParameters = new ArrayList<>();
    }

    @Override
    public void acceptVisitor(VisitorExpression visitorExpression) {
        visitorExpression.visit(this);
    }

    public List<ExpressionNode> getActualParameters() {
        return actualParameters;
    }

    @Override
    public void validate(IType prevType) throws SemanticException {
        IMethod method = getMethod(prevType);

        if (!isInConstructor()
                && isAStaticContext()
                && method.getAccessMode().getName().equals("dynamic")) {

            throw new SemanticException(this, "acceso a metodo dinamico desde contexto estatico");
        }
        validateParameters(method);

        if (getChainedNode() != null) {
            getChainedNode().validate(method.getReturnType());
        }
    }

    private boolean isInConstructor() {
        return SymbolTable.getInstance().getCurrMethod() == SymbolTable.getInstance().getCurrClass().getConstructor();
    }

    private boolean isAStaticContext() {
        IAccessMode accessMode = SymbolTable.getInstance().getCurrMethod().getAccessMode();
        return accessMode.getName().equals("static");
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

    private IMethod getMethod(IType prevType) throws SemanticException {
        if (prevType.getName().equals("void")) {
            throw new SemanticException(this, "acceso invalido: el tipo de retorno anterior es void");
        }

        IClass classType = SymbolTable.getInstance().getClass(prevType.getName());
        if (classType != null) {
            if (!classType.containsMethod(getToken().getLexeme())) {
                throw new SemanticException(this, "no se encontro un metodo con nombre " + getToken().getLexeme());
            } else {
                IMethod out = classType.getMethodMap().get(getToken().getLexeme());
                if (out == null) out = classType.getInheritedMethodMap().get((getToken().getLexeme()));
                return out;
            }
        } else {
            throw new SemanticException(this, "acceso invalido");
        }
    }

    @Override
    public IType getType(IType prevType) throws SemanticException {
        if (getChainedNode() != null) {
            return getChainedNode().getType(getMethod(prevType).getReturnType());
        }
        return getMethod(prevType).getReturnType();
    }

    @Override
    public void validateStatic(IType prevType) throws SemanticException {
        validate(prevType);
        if (!getMethod(prevType).getAccessMode().getName().equals("static")) {
            throw new SemanticException(this, "intento de acceso a metodo dinamico de manera estatica");
        }
    }
}
