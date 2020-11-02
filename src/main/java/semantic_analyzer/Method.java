package semantic_analyzer;

import java.util.HashMap;
import java.util.Map;

public class Method implements IMethod {
    private final String name;
    private final Map<String, IParameter> parameterMap;
    private final IType returnType;
    private final IAccessMode accessMode;

    public Method(IAccessMode accessMode, IType returnType, String name) {
        this.accessMode = accessMode;
        this.returnType = returnType;
        this.name = name;

        parameterMap = new HashMap<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<String, IParameter> getParameterMap() {
        return parameterMap;
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
        return accessMode;
    }
}
