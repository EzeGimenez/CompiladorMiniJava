package semantic_analyzer;

import exceptions.SemanticException;
import semantic_analyzer_ast.CodeBlockNode;

import java.util.List;

public abstract class IMethod extends Entity {

    private CodeBlockNode abstractSyntaxTree;


    public IMethod(String name, String line, int row, int column) {
        super(name, line, row, column);
    }

    public CodeBlockNode getAbstractSyntaxTree() {
        return abstractSyntaxTree;
    }

    public void setAbstractSyntaxTree(CodeBlockNode abstractSyntaxTree) {
        this.abstractSyntaxTree = abstractSyntaxTree;
    }

    public abstract List<IParameter> getParameterList();

    public abstract void addParameter(IParameter parameter);

    public abstract IType getReturnType();

    public abstract IAccessMode getAccessMode();

    @Override
    public void compareTo(Object o) {

    }

    public abstract boolean containsParameter(String parameterName);

    public void validate(IType genericType) throws SemanticException {
        if (getReturnType() != null) getReturnType().validate(genericType);

        for (IParameter p : getParameterList()) {
            p.getType().validate(genericType);
        }
    }

    public void validateOverwrite(IClassType ancestorClassRef, IMethod ancestorMethod) throws SemanticException {
        getAccessMode().compareTo(ancestorMethod.getAccessMode());

        try {
            getReturnType().validateOverwrite(ancestorClassRef, ancestorMethod.getReturnType());
        } catch (SemanticException e) {
            throw new SemanticException(e.getEntity(), "diferente tipo de retorno que metodo ancestro " + ancestorMethod.getName() + " de clase/interfaz " + ancestorClassRef.getName());
        }

        List<IParameter> ancestorParameters = ancestorMethod.getParameterList();
        if (getParameterList().size() != ancestorParameters.size()) {
            throw new SemanticException(this, "diferente cantidad de parametros: " + "se encontraron " + getParameterList().size() + " donde deberia haber " + ancestorParameters.size());
        }
        for (int i = 0; i < getParameterList().size(); i++) {
            try {
                getParameterList().get(i).validateOverwrite(ancestorClassRef, ancestorParameters.get(0));
            } catch (SemanticException e) {
                throw new SemanticException(e.getEntity(),
                        "diferente tipo de parametro que metodo ancestro se esperaba " +
                                ancestorParameters.get(i).getType().getName() +
                                " donde se encontro " +
                                getParameterList().get(i).getType().getName());
            }
        }

    }

    public abstract IMethod cloneForOverwrite(String line, int row, int column);
}
