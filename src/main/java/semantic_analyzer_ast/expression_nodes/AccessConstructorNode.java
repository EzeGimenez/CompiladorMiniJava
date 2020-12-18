package semantic_analyzer_ast.expression_nodes;

import ceivm.IInstructionWriter;
import ceivm.InstructionWriter;
import ceivm.TagProvider;
import exceptions.SemanticException;
import semantic_analyzer.*;
import semantic_analyzer_ast.visitors.VisitorExpression;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AccessConstructorNode extends AccessNode {
    private final List<ExpressionNode> actualParameters;
    private GenericityNode genericityNode;
    private IClass referencedClass;

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
        referencedClass = iClass;
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

    @Override
    public void generateCode() {
        IInstructionWriter writer = InstructionWriter.getInstance();

        int variableCount = getVariableCount();

        writer.write("RMEM", 1, "lugar para la referencia al proximo CIR");
        writer.write("PUSH", variableCount + 2, "k = variables de instancia + 1 (para la VT)");
        writer.write("PUSH", "simple_malloc");
        writer.write("CALL");
        writer.write("DUP", null, "para no perder la referencia");
        writer.write("PUSH", TagProvider.getVtTag(referencedClass.getName()), "etiqueta asociada a la VT");
        writer.write("STOREREF", 0, "guardamos la ref a la VT en el nuevo CIR");
        writer.write("DUP", null, "duplicamos para no perderla luego del constructor y queda para el RA del constructor");

        for (ExpressionNode e : getActualParameters()) {
            e.generateCode();
            writer.write("swap");
        }

        writer.write("PUSH", referencedClass.getConstructor().getTag(), "la direccion del constructor");
        writer.write("CALL", null, "hacemos la llamada");

        if (getChainedNode() != null) {
            getChainedNode().generateCode();
        }
    }

    private int getVariableCount() {
        int instanceVariableCount = -1;
        if (!referencedClass.getAttributeMap().isEmpty()) {
            for (IVariable v : referencedClass.getAttributeMap().values()) {
                instanceVariableCount = Math.max(instanceVariableCount, v.getOffset());
            }
        } else {
            for (IVariable v : referencedClass.getInheritedAttributeMap().values()) {
                instanceVariableCount = Math.max(instanceVariableCount, v.getOffset());
            }
        }
        return instanceVariableCount;
    }
}
