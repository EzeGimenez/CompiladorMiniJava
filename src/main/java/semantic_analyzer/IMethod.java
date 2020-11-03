package semantic_analyzer;

import java.util.Map;

public abstract class IMethod extends Entity {

    public IMethod(String name, String line, int row, int column) {
        super(name, line, row, column);
    }

    public abstract Map<String, IParameter> getParameterMap();

    public abstract void addParameter(IParameter parameter);

    public abstract IType getReturnType();

    public abstract IAccessMode getAccessMode();

    public abstract boolean containsParameter(String parameterName);

    @Override
    public void consolidate() throws SemanticException {

    }
}
