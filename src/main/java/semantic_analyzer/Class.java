package semantic_analyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Class extends IClass {

    private final Collection<IClassReference> interfaceInheritanceList;
    private final Map<String, IVariable> attributeMap;
    private final Map<String, IMethod> methodMap;
    private IClassReference parentClassRef;
    private IMethod constructor;
    private IClassReference genericClassRef;

    public Class(String name, String line, int row, int column) {
        super(name, line, row, column);
        attributeMap = new HashMap<>();
        methodMap = new HashMap<>();
        interfaceInheritanceList = new ArrayList<>();
    }

    @Override
    public IClassReference getParentClassRef() {
        return parentClassRef;
    }

    @Override
    public void setParentClass(IClassReference iClass) {
        parentClassRef = iClass;
    }

    @Override
    public Collection<IClassReference> getInterfaceHierarchyMap() {
        return interfaceInheritanceList;
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
    public IClassReference getGenericClassRef() {
        return genericClassRef;
    }

    @Override
    public void setGenericClassRef(IClassReference genericClassRef) {
        this.genericClassRef = genericClassRef;
    }

    @Override
    public void addInterfaceInheritance(IClassReference iInterface) {
        interfaceInheritanceList.add(iInterface);
    }

    @Override
    public void addAttribute(IVariable attribute) {
        attributeMap.put(attribute.getName(), attribute);
    }

    @Override
    public void addMethod(IMethod method) {
        methodMap.put(method.getName(), method);
    }

    @Override
    public boolean containsAttribute(String name) {
        return attributeMap.containsKey(name);
    }

    @Override
    public boolean containsMethod(String name) {
        return methodMap.containsKey(name);
    }

    @Override
    public boolean containsInterfaceInheritance(String name) {
        for (IClassReference c : interfaceInheritanceList) {
            if (c.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void consolidate() throws SemanticException {
        classInheritanceCheck();
        addInheritedMethods();
    }

    private void addInheritedMethods() throws SemanticException {
        if (parentClassRef != null) {
            IClass iClass = SymbolTable.getInstance().getClass(parentClassRef.getName());
            addMethodsFromClass(iClass);
        }
    }

    private void addMethodsFromClass(IClass iClass) throws SemanticException {
        IMethod methodWithSameName;
        for (IMethod inheritedMethod : iClass.getMethodMap().values()) {
            methodWithSameName = methodMap.get(inheritedMethod.getName());
            if (methodWithSameName != null) {
                try {
                    methodWithSameName.compareTo(inheritedMethod);
                } catch (SemanticException e) {
                    throw new SemanticException(e.getEntity(), "; se intenta cambiar la signatura un metodo heredado: " + e.getMessage());
                }
            } else {
                methodMap.put(inheritedMethod.getName(), inheritedMethod);
            }
        }
    }

    @Override
    public void compareTo(Object o) throws SemanticException {

    }

    @Override
    protected boolean hasAncestor(String name) throws SemanticException {
        if (getName().equals(name)) {
            return true;
        }
        if (parentClassRef == null) { // TODO design concern regarding to create class Object without any parentClasses
            return false;
        }

        IClass parentClass = SymbolTable.getInstance().getClass(parentClassRef.getName());
        if (parentClass != null) {
            return parentClass.hasAncestor(name);
        }

        return false;
    }

    private void classInheritanceCheck() throws SemanticException {
        if (parentClassRef != null) { // TODO design concern regarding to create class Object without any parentClasses
            IClass parentClass = SymbolTable.getInstance().getClass(parentClassRef.getName());
            if (parentClass == null) {
                throw new SemanticException(genericClassRef, "clase no definida");
            }
            if (parentClass.hasAncestor(this.getName())) {
                throw new SemanticException(this, "La clase sufre de herencia circular");
            }
            if (parentClass.getGenericClassRef() != null) {
                if (genericClassRef.getGenericClass() == null) {
                    throw new SemanticException(genericClassRef, "Falta el tipo generico de clase generica");
                }
                if (genericClassRef == null) {
                    throw new SemanticException(this, "La clase hereda de una clase generica sin instanciar o declarar su propio tipo generico");
                }
                IClassReference inheritedInterfaceTypeClass = genericClassRef.getGenericClass().getDeepestMismatchClassRef(getGenericClassRef());
                if (inheritedInterfaceTypeClass != null) {
                    throw new SemanticException(inheritedInterfaceTypeClass, "no definido");
                }
            }
        }
    }

}
