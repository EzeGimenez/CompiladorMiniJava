package semantic_analyzer_ast.expression_nodes;

import ceivm.IInstructionWriter;
import ceivm.InstructionWriter;
import exceptions.SemanticException;
import semantic_analyzer.*;
import semantic_analyzer_ast.sentence_nodes.DeclarationNode;
import semantic_analyzer_ast.visitors.VisitorDeclarationFinder;
import semantic_analyzer_ast.visitors.VisitorExpression;

import java.util.List;

public class AccessVariableNode extends AccessNode {
    private boolean isLeftSide = false, isLocalVariable;
    private int offset = -99;

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
            offset = parameter.getOffset();
            isLocalVariable = true;
            return parameter.getType();
        }
        DeclarationNode declarationNode = getLocalDeclaration();
        if (declarationNode != null) {
            isLocalVariable = true;
            return declarationNode.getType();
        }
        IVariable variable = getAttribute();
        if (variable != null) {
            offset = variable.getOffset();
            isLocalVariable = false;
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
        VisitorDeclarationFinder visitorDeclarationFinder = new VisitorDeclarationFinder(getToken().getLexeme());

        List<DeclarationNode> declarationNodeList = SymbolTable
                .getInstance()
                .getCurrMethod()
                .getAbstractSyntaxTree()
                .getCurrentDeclarations();

        int i = 0;
        for (DeclarationNode node : declarationNodeList) {
            node.acceptVisitor(visitorDeclarationFinder);
            if (visitorDeclarationFinder.getDeclarationNodeFound() != null) {
                offset = -i;
                break;
            }
            i++;
        }

        return visitorDeclarationFinder.getDeclarationNodeFound();
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
            offset = parameter.getOffset();
            isLocalVariable = true;
            return;
        }
        DeclarationNode declarationNode = getLocalDeclaration();
        if (declarationNode != null) {
            validateChainedNode();
            isLocalVariable = true;
            return;
        }
        IVariable variable = getAttribute();
        if (variable != null) {
            if (isStaticMethod() && variable.getAccessMode() == null) {
                throw new SemanticException(this, "acceso a atributo de instancia desde contexto estatico");
            }
            attributeCheck(variable);
            validateChainedNode();
            offset = variable.getOffset();
            isLocalVariable = false;
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
        return accessMode != null && accessMode.getName().equals("static");
    }

    public void setIsLeftSide() {
        isLeftSide = true;
    }

    @Override
    public void generateCode() {
        IInstructionWriter writer = InstructionWriter.getInstance();
        if (!isLocalVariable) {
            writer.write("load", 3, "cargamos el this");
            if (!isLeftSide || getChainedNode() != null) {
                writer.write("loadref", offset);
            } else {
                writer.write("swap");
                writer.write("storeref", offset);
            }
        } else {
            if (!isLeftSide || getChainedNode() != null) {
                writer.write("load", offset);
            } else {
                writer.write("store", offset);
            }
        }

        if (getChainedNode() != null) {
            getChainedNode().generateCode();
        }
    }

}
