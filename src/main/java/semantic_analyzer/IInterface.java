package semantic_analyzer;

import java.util.Collection;
import java.util.Map;

public abstract class IInterface extends Entity {

    public IInterface(String name, String line, int row, int column) {
        super(name, line, row, column);
    }

    public abstract Collection<IClassReference> getInheritance();

    public abstract void addInheritance(IClassReference iInterface);

    public abstract IClassReference getGenericClass();

    public abstract void setGenericClass(IClassReference genericClass);

    public abstract Map<String, IMethod> getMethodMap();

    public abstract void addMethod(IMethod method);

    public abstract boolean containsMethod(String name);

    protected abstract boolean hasAncestor(String name);
}
