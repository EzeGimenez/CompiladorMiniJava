package semantic_analyzer;

import java.util.LinkedHashMap;
import java.util.Map;

public class Constructor extends IMethod {
    private final Map<String, IParameter> parameterMap;
    private final IType returnType;

    public Constructor(String name, IType returnType, String line, int row, int column) {
        super(name, line, row, column);
        this.returnType = returnType;
        parameterMap = new LinkedHashMap<>();
    }

    @Override
    public void addParameter(IParameter parameter) {
        parameterMap.put(parameter.getName(), parameter);
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
        return parameterMap.containsKey(parameterName);
    }

    @Override
    public Map<String, IParameter> getParameterMap() {
        return parameterMap;
    }
}
