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

public class AccessMethodNode extends AccessNode {
    private final List<ExpressionNode> actualParameters;
    private IMethod referencedMethod;
    private String className;

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
        referencedMethod = getMethod();
        if (!isInConstructor()
                && isAStaticContext()
                && !isStaticMethod()) {

            throw new SemanticException(this, "acceso a metodo dinamico desde contexto estatico");
        }
        validateParameters(referencedMethod);
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
            expressionNode.validate();
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
            className = classType.getName();
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
                className = iInterface.getName();
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
        if (referencedMethod == null) {
            referencedMethod = getMethod();
        }
        return referencedMethod.getReturnType();
    }

    @Override
    public void generateCode() {
        IInstructionWriter writer = InstructionWriter.getInstance();
        if (!isStaticMethod()) {
            writer.write("load", 3, "futuro this");
            if (!referencedMethod.getReturnType().getName().equals("void")) {
                writer.write("rmem", 1, "espacio para el return");
                writer.write("swap");
            }

            for (ExpressionNode e : getActualParameters()) {
                e.generateCode();
                writer.write("swap");
            }

            writer.write("dup", null, "duplicamos para no perderlo");
            writer.write("loadref", 0, "carga de la vt");
            writer.write("loadref", referencedMethod.getOffset(), "cargo la direccion del metodo");
            writer.write("call");

        } else {
            if (!referencedMethod.getReturnType().getName().equals("void")) {
                writer.write("rmem", 1, "espacio para el return");
            }

            for (ExpressionNode e : getActualParameters()) {
                e.generateCode();
            }

            writer.write("push", TagProvider.getMethodTag(className, referencedMethod.getName()));
            writer.write("call");
        }
        if (getChainedNode() != null) {
            getChainedNode().generateCode();
        }
    }

    private boolean isStaticMethod() {
        return referencedMethod.getAccessMode().getName().equals("static");
    }

    public IMethod getReferencedMethod() {
        return referencedMethod;
    }
}
