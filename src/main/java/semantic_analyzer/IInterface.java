package semantic_analyzer;

import java.util.Collection;
import java.util.Map;

public interface IInterface {
    Collection<IClassReference> getInheritance();

    void addInheritance(IClassReference iInterface);

    String getGenericClass();

    String getName();

    Map<String, IMethod> getMethodMap();

    void addMethod(IMethod method);

    void setGenericClass(String genericClass);
}
