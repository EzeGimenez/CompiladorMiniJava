package semantic_analyzer;

import exceptions.SemanticException;

import java.util.Collection;
import java.util.Map;

public abstract class IClass extends Entity {

    public IClass(String name, String line, int row, int column) {
        super(name, line, row, column);
    }

    public abstract IClassType getParentClass();

    public abstract void setParentClass(IClassType iClass);

    public abstract Collection<IClassType> getInterfaceInheritanceList();

    public abstract Map<String, IVariable> getAttributeMap();

    public abstract Map<String, IMethod> getMethodMap();

    public abstract IMethod getConstructor();

    public abstract void setConstructor(IMethod constructor);

    public abstract IType getGenericType();

    public abstract void setGenericType(IClassType genericClassRef);

    public abstract void addInterfaceInheritance(IClassType iInterface);

    public abstract void addAttribute(IVariable attribute);

    public abstract void addMethod(IMethod method);

    public abstract boolean containsAttribute(String name);

    public abstract boolean containsMethod(String name);

    public abstract boolean containsInterfaceInheritance(String name);

    protected abstract boolean hasAncestor(String name) throws SemanticException;

    public abstract void consolidate() throws SemanticException;
}
