package semantic_analyzer;

import java.util.List;

public abstract class IMethod extends Entity {

    public IMethod(String name, String line, int row, int column) {
        super(name, line, row, column);
    }

    public abstract List<IParameter> getParameterList();

    public abstract void addParameter(IParameter parameter);

    public abstract IType getReturnType();

    public abstract IAccessMode getAccessMode();

    public abstract boolean containsParameter(String parameterName);

    public void validate(IClassReference genericTypeRef) throws SemanticException {
        if (getReturnType() != null) validateType(genericTypeRef, getReturnType());
        for (IParameter p : getParameterList()) {
            validateType(genericTypeRef, p.getType());
        }
    }

    private void validateType(IClassReference genericType, IType type) throws SemanticException {
        type.validate(genericType);
    }
}
