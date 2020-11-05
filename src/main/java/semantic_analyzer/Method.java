package semantic_analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Method extends IMethod {

    private final List<IParameter> parameterList;
    private final IType returnType;
    private final IAccessMode accessMode;

    public Method(IAccessMode accessMode, IType returnType, String name) {
        this(accessMode, returnType, name, "", 0, 0);
    }

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

}
