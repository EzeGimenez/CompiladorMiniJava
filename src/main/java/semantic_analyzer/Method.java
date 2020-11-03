package semantic_analyzer;

import java.util.LinkedHashMap;
import java.util.Map;

public class Method extends IMethod {

    private final Map<String, IParameter> parameterMap;
    private final IType returnType;
    private final IAccessMode accessMode;

    public Method(IAccessMode accessMode, IType returnType, String name, String line, int row, int column) {
        super(name, line, row, column);
        this.accessMode = accessMode;
        this.returnType = returnType;

        parameterMap = new LinkedHashMap<>();
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

    @Override
    public boolean containsParameter(String parameterName) {
        return parameterMap.containsKey(parameterName);
    }

    @Override
    public void consolidate() throws SemanticException {

    }
}
