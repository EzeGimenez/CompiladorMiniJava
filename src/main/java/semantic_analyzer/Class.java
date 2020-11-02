package semantic_analyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Class implements IClass {
    private final String name;
    private final Collection<String> interfaceHierarchyList;
    private final Map<String, IVariable> attributeMap;
    private final Map<String, IMethod> methodMap;
    private String classHierarchy;
    private IConstructor constructor;
    private String genericClass; // TODO may be interface instead

    public Class(String name) {
        this.name = name;
        attributeMap = new HashMap<>();
        methodMap = new HashMap<>();
        interfaceHierarchyList = new ArrayList<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getClassHierarchy() {
        return classHierarchy;
    }

    @Override
    public void setClassHierarchy(String iClass) {
        classHierarchy = iClass;
    }

    @Override
    public Collection<String> getInterfaceHierarchyMap() {
        return interfaceHierarchyList;
    }

    @Override
    public Map<String, IVariable> getAttributeMap() {
        return attributeMap;
    }

    @Override
    public Map<String, IMethod> getMethodMap() {
        return methodMap;
    }

    @Override
    public IMethod getConstructor() {
        return constructor;
    }

    @Override
    public void setConstructor(IMethod constructor) {
        this.constructor = constructor;
    }

    @Override
    public String getGenericClass() {
        return genericClass;
    }

    @Override
    public void setGenericClass(String genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    public void addInterfaceHierarchy(String iInterface) {
        interfaceHierarchyList.add(iInterface);
    }

    @Override
    public void addAttribute(IVariable attribute) {
        attributeMap.put(attribute.getName(), attribute);
    }

    @Override
    public void addMethod(IMethod method) {
        methodMap.put(method.getName(), method);
    }
}
