package semantic_analyzer_ast.expression_nodes;

import exceptions.SemanticException;
import semantic_analyzer.*;
import semantic_analyzer_ast.sentence_nodes.DeclarationNode;
import semantic_analyzer_ast.visitors.VisitorDeclarationFinder;
import semantic_analyzer_ast.visitors.VisitorExpression;

import java.util.List;

public class AccessVariableNode extends AccessNode {
    public AccessVariableNode(String line, int row, int column) {
        super(line, row, column);
    }

    @Override
    public IType getType() throws SemanticException {
        if (getChainedNode() != null) {
            return getChainedNode().getType(getCurrentType());
        }
        return getCurrentType();
    }

    @Override
    public IType getCurrentType() throws SemanticException {
        IParameter parameter = getParameter();
        if (parameter != null) {
            return parameter.getType();
        }
        DeclarationNode declarationNode = getLocalDeclaration();
        if (declarationNode != null) {
            return declarationNode.getType();
        }
        IVariable variable = getAttribute();
        if (variable != null) {
            return variable.getType();
        }
        throw new SemanticException(this, "variable no definida o inaccesible");
    }

    private IParameter getParameter() {
        return SymbolTable.getInstance().getCurrMethod().getParameter(getToken().getLexeme());
    }

    private IVariable getAttribute() throws SemanticException {
        IClass classType = SymbolTable.getInstance().getCurrClass();
        if (classType != null) {
            if (classType.containsAttribute(getToken().getLexeme())) {
                IVariable out = classType.getAttributeMap().get(getToken().getLexeme());
                if (out != null) {
                    return out;
                }
                return classType.getInheritedAttributeMap().get(getToken().getLexeme());
            }
            return null;
        } else {
            throw new SemanticException(this, "clase no encontrada");
        }
    }

    private DeclarationNode getLocalDeclaration() {
        List<DeclarationNode> declarationNodeList = SymbolTable.getInstance().getCurrMethod().getAbstractSyntaxTree().getCurrentDeclarations();
        VisitorDeclarationFinder visitorDeclarationFinder = new VisitorDeclarationFinder(getToken().getLexeme());
        for (DeclarationNode node : declarationNodeList) {
            node.acceptVisitor(visitorDeclarationFinder);
        }
        if (visitorDeclarationFinder.getDeclarationNodeFound() != null) {
            return visitorDeclarationFinder.getDeclarationNodeFound();
        }
        return null;
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
        IParameter parameter = getParameter();
        if (parameter != null) {
            validateChainedNode();
            return;
        }
        DeclarationNode declarationNode = getLocalDeclaration();
        if (declarationNode != null) {
            validateChainedNode();
            return;
        }
        IVariable variable = getAttribute();
        if (variable != null) {
            if (isStaticMethod() && variable.getAccessMode() == null) {
                throw new SemanticException(this, "acceso a atributo de instancia desde contexto estatico");
            }
            attributeCheck(variable);
            validateChainedNode();
            return;
        }
        throw new SemanticException(this, "variable no definida o inaccesible");
    }

    private void attributeCheck(IVariable variable) throws SemanticException {
        IClass classType = SymbolTable.getInstance().getCurrClass();
        if (classType.getInheritedAttributeMap().containsValue(variable)) {
            if (variable.getVisibility().getName().equals("private")) {
                throw new SemanticException(this, "intento de acceso a variable privada");
            }
        }
    }

    private boolean isStaticMethod() {
        IAccessMode accessMode = SymbolTable.getInstance().getCurrMethod().getAccessMode();
        return accessMode.getName().equals("static");
    }
}
