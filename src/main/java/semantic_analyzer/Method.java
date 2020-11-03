package semantic_analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Method extends IMethod {

    private final List<IParameter> parameterList;
    private final IType returnType;
    private final IAccessMode accessMode;

    public Method(IAccessMode accessMode, IType returnType, String name, String line, int row, int column) {
        super(name, line, row, column);
        this.accessMode = accessMode;
        this.returnType = returnType;

        parameterList = new ArrayList<>();
    }

    @Override
    public List<IParameter> getParameterList() {
        return parameterList;
    }

    @Override
    public void addParameter(IParameter parameter) {
        parameterList.add(parameter);
    }

    @Override
    public IType getReturnType() {
        return returnType;
    }

    @Override
    public IAccessMode getAccessMode() {
        return accessMode;
    }

    @Override
    public boolean containsParameter(String parameterName) {
        for (IParameter p : parameterList) {
            if (Objects.equals(p.getName(), parameterName)) return true;
        }
        return false;
    }

    @Override
    public void consolidate() throws SemanticException {

    }

    @Override
    public void compareTo(Object o) throws SemanticException {

        if (o == null || getClass() != o.getClass()) throw new SemanticException(this, "diferentes");

        Method method = (Method) o;
        if (!getName().equals(method.getName())) throw new SemanticException(this, "nombre diferente");

        accessMode.compareTo(method.getAccessMode());
        if (returnType != null) {
            try {
                returnType.compareTo(method.getReturnType());
            } catch (SemanticException e) {
                throw new SemanticException(e.getEntity(), "diferente tipo de retorno");
            }
        }

        List<IParameter> methodParameters = method.getParameterList();
        if (parameterList.size() != methodParameters.size()) {
            throw new SemanticException(this, "diferente cantidad de parametros: " + "se encontraron " + parameterList.size() + " donde deberia haber " + methodParameters.size());
        }
        for (int i = 0; i < parameterList.size(); i++) {
            parameterList.get(0).compareTo(methodParameters.get(0));
        }
    }

}
