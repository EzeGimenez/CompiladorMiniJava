package semantic_analyzer;

import java.util.HashMap;
import java.util.Map;

public class Constructor implements IMethod {

    private final String name;
    private final Map<String, IParameter> parameterMap;
    private final IType returnType;

    public Constructor(String name, IType returnType) {
        this.name = name;
        this.returnType = returnType;
        parameterMap = new HashMap<>();
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
    public Map<String, IParameter> getParameterMap() {
        return parameterMap;
    }

    @Override
    public String getName() {
        return name;
    }
}
