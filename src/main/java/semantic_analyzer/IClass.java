package semantic_analyzer;

import java.util.Collection;
import java.util.Map;

public interface IClass {

    String getName();

    String getClassHierarchy();

    void setClassHierarchy(IClassReference iClass);

    Collection<String> getInterfaceHierarchyMap();

    Map<String, IVariable> getAttributeMap();

    Map<String, IMethod> getMethodMap();

    IMethod getConstructor();

    void setConstructor(IMethod constructor);

    String getGenericClass();

    void setGenericClass(String genericClass);

    void addInterfaceHierarchy(IClassReference iInterface);

    void addAttribute(IVariable attribute);

    void addMethod(IMethod method);
}
