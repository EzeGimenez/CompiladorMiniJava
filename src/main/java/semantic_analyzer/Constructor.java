package semantic_analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Constructor extends IMethod {
    private final List<IParameter> parameterList;
    private final IType returnType;

    public Constructor(String name, IType returnType, String line, int row, int column) {
        super(name, line, row, column);
        this.returnType = returnType;
        parameterList = new ArrayList<>();
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
        return null;
    }

    @Override
    public boolean containsParameter(String parameterName) {
        for (IParameter p : parameterList) {
            if (Objects.equals(p.getName(), parameterName)) return true;
        }
        return false;
    }

    @Override
    public List<IParameter> getParameterList() {
        return parameterList;
    }

    @Override
    public void compareTo(Object o) throws SemanticException {

    }
}
