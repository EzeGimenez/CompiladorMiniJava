package semantic_analyzer;

import java.util.Collection;
import java.util.Map;

public abstract class IClass extends Entity {

    public IClass(String name, String line, int row, int column) {
        super(name, line, row, column);
    }

    public abstract IClassReference getParentClassRef();

    public abstract void setParentClassRef(IClassReference iClass);

    public abstract Collection<IClassReference> getInterfaceHierarchyMap();

    public abstract Map<String, IVariable> getAttributeMap();

    public abstract Map<String, IMethod> getMethodMap();

    public abstract IMethod getConstructor();

    public abstract void setConstructor(IMethod constructor);

    public abstract IClassReference getGenericClassRef();

    public abstract void setGenericClassRef(IClassReference genericClassRef);

    public abstract void addInterfaceInheritance(IClassReference iInterface);

    public abstract void addAttribute(IVariable attribute);

    public abstract void addMethod(IMethod method);

    public abstract boolean containsAttribute(String name);

    public abstract boolean containsMethod(String name);

    public abstract boolean containsInterfaceInheritance(String name);

    protected abstract boolean hasAncestor(String name) throws SemanticException;
}
