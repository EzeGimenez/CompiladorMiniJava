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

    private IType getCurrentType() throws SemanticException {
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
        throw new SemanticException(this, "variable no definida");
    }

    private IParameter getParameter() {
        return SymbolTable.getInstance().getCurrMethod().getParameter(getToken().getLexeme());
    }

    private IVariable getAttribute() {
        return SymbolTable.getInstance().getCurrClass().getAttributeMap().get(getToken().getLexeme());
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
    public void validateForAssignment() throws SemanticException {

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
            return;
        }
        DeclarationNode declarationNode = getLocalDeclaration();
        if (declarationNode != null) {
            return;
        }
        IVariable variable = getAttribute();
        if (variable != null) {
            if (isStaticMethod() && variable.getAccessMode().getName().equals("dynamic")) {
                throw new SemanticException(this, "acceso a variable dinamica desde contexto estatico");
            }
        }
        throw new SemanticException(this, "variable no definida");
    }

    private boolean isStaticMethod() {
        IAccessMode accessMode = SymbolTable.getInstance().getCurrMethod().getAccessMode();
        return accessMode.getName().equals("static");
    }
}
