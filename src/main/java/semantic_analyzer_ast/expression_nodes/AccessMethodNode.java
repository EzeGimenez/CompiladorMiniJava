package semantic_analyzer_ast.expression_nodes;

import exceptions.SemanticException;
import semantic_analyzer.*;
import semantic_analyzer_ast.visitors.VisitorExpression;

import java.util.ArrayList;
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
    public void validateForAssignment() throws SemanticException {
        throw new SemanticException(this, "asignacion a un metodo");
    }

    @Override
    public void validate() throws SemanticException {
        if (getChainedNode() == null) {
            if (isStaticMethod() && getMethodAccessMode().equals("dynamic")) {
                throw new SemanticException(this, "acceso a metodo dinamico desde contexto estatico");
            }
        }
    }

    private String getMethodAccessMode() throws SemanticException {
        IClass currClass = SymbolTable.getInstance().getCurrClass();
        IMethod method = currClass.getMethodMap().get(getToken().getLexeme());
        if (method == null) {
            throw new SemanticException(this, "metodo no definido");
        } else {
            return method.getAccessMode().getName();
        }
    }

    private boolean isStaticMethod() {
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

    private IType getCurrentType() throws SemanticException {
        IClass currClass = SymbolTable.getInstance().getCurrClass();
        IMethod method = currClass.getMethodMap().get(getToken().getLexeme());
        if (method == null) {
            throw new SemanticException(this, "metodo no definido");
        }
        return method.getReturnType();
    }

}
