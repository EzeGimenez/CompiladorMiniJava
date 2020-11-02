package semantic_analyzer;

import java.util.Map;

public interface IMethod {
    String getName();

    Map<String, IParameter> getParameterMap();

    void addParameter(IParameter parameter);

    IType getReturnType();

    IAccessMode getAccessMode();
}
