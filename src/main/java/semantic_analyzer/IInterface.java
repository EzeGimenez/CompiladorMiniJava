package semantic_analyzer;

import exceptions.SemanticException;

import java.util.Collection;
import java.util.Map;

public abstract class IInterface extends Entity {

    public IInterface(String name, String line, int row, int column) {
        super(name, line, row, column);
    }

    public abstract Collection<IClassType> getInheritance();

    public abstract void addInheritance(IClassType iInterface);

    public abstract IType getGenericType();

    public abstract void setGenericType(IClassType genericType);

    public abstract Map<String, IMethod> getMethodMap();

    public abstract void addMethod(IMethod method);

    public abstract boolean containsMethod(String name);

    protected abstract boolean hasAncestor(String name);

    public abstract void declarationCheck() throws SemanticException;
}
