package semantic_analyzer_ast.expression_nodes;

import ceivm.IInstructionWriter;
import ceivm.InstructionWriter;
import exceptions.SemanticException;
import semantic_analyzer.*;
import semantic_analyzer_ast.visitors.VisitorExpression;

public class ChainedVariableNode extends ChainedNode {

    private boolean isLeftSide = false;
    private IVariable referencedVariable;

    public ChainedVariableNode(String line, int row, int column) {
        super(line, row, column);
    }

    @Override
    public void acceptVisitor(VisitorExpression visitorExpression) {
        visitorExpression.visit(this);
    }

    private IVariable getAttribute(IType prevType) throws SemanticException {
        if (prevType.getName().equals("void")) {
            throw new SemanticException(this, "acceso invalido: el tipo de retorno anterior es void");
        }

        IClass classType = SymbolTable.getInstance().getClass(prevType.getName());
        if (classType != null) {
            if (!classType.containsAttribute(getToken().getLexeme())) {
                throw new SemanticException(this, "no se encontro un atributo con nombre " + getToken().getLexeme());
            } else {
                IVariable out = classType.getAttributeMap().get(getToken().getLexeme());
                if (out == null) out = classType.getInheritedAttributeMap().get(getToken().getLexeme());
                if (out != null) {
                    return out;
                }
                return classType.getInheritedAttributeMap().get(getToken().getLexeme());
            }
        } else {
            throw new SemanticException(this, "acceso invalido");
        }
    }

    @Override
    public void validate(IType prevType) throws SemanticException {
        referencedVariable = getAttribute(prevType);
        visibilityCheck(referencedVariable);
        if (isStaticAttribute() && referencedVariable.getAccessMode() == null) {
            throw new SemanticException(this, "acceso a variable dinamica dentro de metodo estatico");
        }
        if (getChainedNode() != null) {
            getChainedNode().validate(getAttribute(prevType).getType());
        }
    }

    private void visibilityCheck(IVariable attribute) throws SemanticException {
        IVisibility visibility = attribute.getVisibility();
        if (!visibility.getName().equals("public")) {
            throw new SemanticException(this, "variable no visible en este contexto");
        }
    }

    private boolean isStaticAttribute() {
        IAccessMode accessMode = SymbolTable.getInstance().getCurrMethod().getAccessMode();
        return accessMode != null && accessMode.getName().equals("static");
    }

    @Override
    public IType getType(IType prevType) throws SemanticException {
        if (referencedVariable == null) {
            referencedVariable = getAttribute(prevType);
        }
        if (getChainedNode() != null) {
            return getChainedNode().getType(getAttribute(prevType).getType());
        }
        return getAttribute(prevType).getType();
    }

    @Override
    public void validateStatic(IType prevType) throws SemanticException {
        validate(prevType);
        if (!getAttribute(prevType).getAccessMode().getName().equals("static")) {
            throw new SemanticException(this, "intento de acceso a metodo dinamico de manera estatica");
        }
    }

    public void setIsLeftSide() {
        isLeftSide = true;
    }

    @Override
    public void generateCode() {
        IInstructionWriter writer = InstructionWriter.getInstance();
        if (!isLeftSide || getChainedNode() != null) {
            writer.write("loadref", referencedVariable.getOffset());
        } else {
            writer.write("swap");
            writer.write("storeref", referencedVariable.getOffset());
        }
    }
}
