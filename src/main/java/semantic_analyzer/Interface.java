package semantic_analyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Interface implements IInterface {

    private final String name;
    private final Map<String, IMethod> methodMap;
    private final Collection<IClassReference> inheritance;
    private String genericClass;

    public Interface(String name) {
        this.name = name;
        methodMap = new HashMap<>();
        inheritance = new ArrayList<>();
    }

    @Override
    public Collection<IClassReference> getInheritance() {
        return inheritance;
    }

    @Override
    public void addInheritance(IClassReference iInterface) {
        inheritance.add(iInterface);
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
    public String getName() {
        return name;
    }

    @Override
    public Map<String, IMethod> getMethodMap() {
        return methodMap;
    }

    @Override
    public void addMethod(IMethod method) {
        methodMap.put(method.getName(), method);
    }

}
