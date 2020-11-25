package semantic_analyzer_ast.expression_nodes;

import exceptions.SemanticException;
import semantic_analyzer.IAccessMode;
import semantic_analyzer.IClass;
import semantic_analyzer.IType;
import semantic_analyzer.SymbolTable;
import semantic_analyzer_ast.visitors.VisitorExpression;

public class ChainedVariableNode extends ChainedNode {

    public ChainedVariableNode(String line, int row, int column) {
        super(line, row, column);
    }

    @Override
    public IType getType() throws SemanticException {
        return null;
    }

    @Override
    public void validateForAssignment() throws SemanticException {

    }

    @Override
    public void validateForAssignemnt(IType prevType) throws SemanticException {
        if (getChainedNode() != null) {
            getChainedNode().validateForAssignemnt(getType(prevType));
        } else {
            getCurrentType(prevType);
        }
    }

    @Override
    public void acceptVisitor(VisitorExpression visitorExpression) {
        visitorExpression.visit(this);
    }

    private IType getCurrentType(IType prevType) throws SemanticException {
        IClass classType = SymbolTable.getInstance().getClass(prevType.getName());
        if (classType != null) {
            if (!classType.containsAttribute(getToken().getLexeme())) {
                throw new SemanticException(this, "no se encontró un atributo con nombre " + getToken().getLexeme());
            } else {
                return classType.getAttributeMap().get(getToken().getLexeme()).getType();
            }
        } else {
            throw new SemanticException(this, "clase no encontrada");
        }
    }

    @Override
    public void validate() throws SemanticException {

    }

    @Override
    public void validate(IType prevType) throws SemanticException {
        if (getChainedNode() != null) {
            getChainedNode().validate(getCurrentType(prevType));
        } else {
            if (isStaticMethod() && getVariableAccessMode(prevType).equals("dynamic")) {
                throw new SemanticException(this, "acceso a variable dinámica dentro de metodo estático");
            }
        }
    }

    private boolean isStaticMethod() {
        IAccessMode accessMode = SymbolTable.getInstance().getCurrMethod().getAccessMode();
        return accessMode.getName().equals("static");
    }

    private String getVariableAccessMode(IType prevType) throws SemanticException {
        IClass classType = SymbolTable.getInstance().getClass(prevType.getName());
        if (classType != null) {
            if (!classType.containsAttribute(getToken().getLexeme())) {
                throw new SemanticException(this, "no se encontró una variable con nombre " + getToken().getLexeme());
            } else {
                return classType.getAttributeMap().get(getToken().getLexeme()).getAccessMode().getName();
            }
        } else {
            throw new SemanticException(this, "clase no encontrada");
        }
    }

    @Override
    public IType getType(IType prevType) throws SemanticException {
        if (getChainedNode() != null) {
            return getChainedNode().getType(getCurrentType(prevType));
        }
        return getCurrentType(prevType);
    }
}
