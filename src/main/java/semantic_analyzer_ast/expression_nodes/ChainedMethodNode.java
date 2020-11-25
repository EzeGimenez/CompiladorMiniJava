package semantic_analyzer_ast.expression_nodes;

import exceptions.SemanticException;
import semantic_analyzer.IAccessMode;
import semantic_analyzer.IClass;
import semantic_analyzer.IType;
import semantic_analyzer.SymbolTable;
import semantic_analyzer_ast.visitors.VisitorExpression;

import java.util.ArrayList;
import java.util.List;

public class ChainedMethodNode extends ChainedNode {
    private final List<ExpressionNode> actualParameters;

    public ChainedMethodNode(String line, int row, int column) {
        super(line, row, column);
        actualParameters = new ArrayList<>();
    }

    @Override
    public void validate() throws SemanticException {

    }

    @Override
    public IType getType() throws SemanticException {
        return null;
    }

    @Override
    public void validateForAssignment() throws SemanticException {

    }

    @Override
    public void acceptVisitor(VisitorExpression visitorExpression) {
        visitorExpression.visit(this);
    }

    @Override
    public void validateForAssignemnt(IType prevType) throws SemanticException {
        validate(prevType);
        if (getChainedNode() != null) {
            getChainedNode().validateForAssignemnt(getType(prevType));
        } else {
            throw new SemanticException(this, "asignacion a un método");
        }
    }

    public List<ExpressionNode> getActualParameters() {
        return actualParameters;
    }

    @Override
    public void validate(IType prevType) throws SemanticException {
        if (getChainedNode() != null) {
            getChainedNode().validate(getCurrentType(prevType));
        } else {
            if (isStaticMethod() && getMethodAccessMode(prevType).equals("dynamic")) {
                throw new SemanticException(this, "acceso a metodo dinámico dentro de metodo estatico");
            }
        }
    }

    private String getMethodAccessMode(IType prevType) throws SemanticException {
        IClass classType = SymbolTable.getInstance().getClass(prevType.getName());
        if (classType != null) {
            if (!classType.containsMethod(getToken().getLexeme())) {
                throw new SemanticException(this, "no se encontró un metodo con nombre " + getToken().getLexeme());
            } else {
                return classType.getMethodMap().get(getToken().getLexeme()).getAccessMode().getName();
            }
        } else {
            throw new SemanticException(this, "clase no encontrada");
        }
    }

    private boolean isStaticMethod() {
        IAccessMode accessMode = SymbolTable.getInstance().getCurrMethod().getAccessMode();
        return accessMode.getName().equals("static");
    }

    @Override
    public IType getType(IType prevType) throws SemanticException {
        if (getChainedNode() != null) {
            return getChainedNode().getType(getCurrentType(prevType));
        }
        return getCurrentType(prevType);
    }

    private IType getCurrentType(IType prevType) throws SemanticException {
        IClass classType = SymbolTable.getInstance().getClass(prevType.getName());
        if (classType != null) {
            if (!classType.containsMethod(getToken().getLexeme())) {
                throw new SemanticException(this, "no se encontró un metodo con nombre " + getToken().getLexeme());
            } else {
                return classType.getMethodMap().get(getToken().getLexeme()).getReturnType();
            }
        } else {
            throw new SemanticException(this, "clase no encontrada");
        }
    }
}
