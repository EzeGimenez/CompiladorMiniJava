package semantic_analyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Class extends IClass {

    private final Collection<IClassType> interfaceInheritanceList;
    private final Map<String, IVariable> attributeMap;
    private final Map<String, IMethod> methodMap;
    private IClassType parentClass;
    private IMethod constructor;
    private IType genericType;
    private boolean didConsolidate;

    public Class(String name) {
        this(name, "", 0, 0);
    }

    public Class(String name, String line, int row, int column) {
        super(name, line, row, column);
        attributeMap = new HashMap<>();
        methodMap = new HashMap<>();
        interfaceInheritanceList = new ArrayList<>();
        didConsolidate = false;
    }

    @Override
    public IClassType getParentClass() {
        return parentClass;
    }

    @Override
    public void setParentClass(IClassType iClass) {
        parentClass = iClass;
    }

    @Override
    public Collection<IClassType> getInterfaceHierarchyMap() {
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
    public IType getGenericType() {
        return genericType;
    }

    @Override
    public void setGenericType(IClassType genericClassRef) {
        this.genericType = genericClassRef;
    }

    @Override
    public void addInterfaceInheritance(IClassType iInterface) {
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
        for (IClassType c : interfaceInheritanceList) {
            if (c.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean hasAncestor(String name) throws SemanticException {
        if (getName().equals(name)) {
            return true;
        }
        if (parentClass == null) { // TODO design concern regarding to create class Object without any parentClasses
            return false;
        }

        IClass parentClass = SymbolTable.getInstance().getClass(this.parentClass.getName());
        if (parentClass != null) {
            return parentClass.hasAncestor(name);
        }

        return false;
    }

    @Override
    public void compareTo(Object o) throws SemanticException {

    }

    @Override
    public void consolidate() throws SemanticException {
        if (!didConsolidate) {
            didConsolidate = true;
            validateClassInheritance();
            validateInterfaceInheritance();
            consolidateAncestors();
            addConstructorIfMissing();
            addInheritedMethodsFromParentClass();
            addInheritedMethodsFromInterfaces();
            addInheritedAttributesFromParentClass();
            validateMethods();
            validateAttributes();
        }
    }

    private void consolidateAncestors() throws SemanticException {
        if (parentClass != null) {
            IClass parentClass = SymbolTable.getInstance().getClass(this.parentClass.getName());
            parentClass.consolidate();
        }

        for (IClassType c : interfaceInheritanceList) {
            IInterface iInterface = getInterfaceForReference(c);
            iInterface.consolidate();
        }
    }

    private void validateClassInheritance() throws SemanticException {
        if (parentClass != null) {
            parentClass.validate(genericType);

            IClass classForParent = getClassForReference(parentClass);
            if (classForParent.hasAncestor(this.getName())) {
                throw new SemanticException(this, "La clase sufre de herencia circular");
            }
        }
    }

    private void validateInterfaceInheritance() throws SemanticException {
        for (IClassType interfaceRef : interfaceInheritanceList) {
            interfaceRef.validate(genericType);
        }
    }

    private void addConstructorIfMissing() {
        if (constructor == null) {
            IClassType classType = new ClassType(getName());
            constructor = new Constructor(getName(), classType);
        }
    }

    private void addInheritedMethodsFromParentClass() throws SemanticException {
        if (parentClass != null) {
            IClass iClass = getClassForReference(parentClass);
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
                    throw new SemanticException(e.getEntity(), "; se intenta cambiar la signatura a un metodo heredado: " + e.getMessage());
                }
            } else {
                methodMap.put(inheritedMethod.getName(), inheritedMethod);
            }
        }
    }

    private IInterface getInterfaceForReference(IClassType c) {
        return SymbolTable.getInstance().getInterface(c.getName());
    }

    private IClass getClassForReference(IClassType c) {
        return SymbolTable.getInstance().getClass(c.getName());
    }

    private void addInheritedMethodsFromInterfaces() throws SemanticException {
        for (IClassType c : interfaceInheritanceList) {
            IInterface iInterface = getInterfaceForReference(c);
            addMethodsFromInterface(iInterface);
        }
    }

    private void addMethodsFromInterface(IInterface iInterface) throws SemanticException {
        IMethod methodWithSameName;
        for (IMethod inheritedMethod : iInterface.getMethodMap().values()) {
            methodWithSameName = methodMap.get(inheritedMethod.getName());
            if (methodWithSameName != null) {
                try {
                    methodWithSameName.compareTo(inheritedMethod);
                } catch (SemanticException e) {
                    throw new SemanticException(e.getEntity(), "se intenta cambiar la signatura a un metodo heredado: " + e.getMessage());
                }
            } else {
                SemanticException methodNotImplException = new SemanticException(this,
                        "la clase " + getName() + " no implementa el metodo "
                                + inheritedMethod.getName() +
                                " de la interfaz " +
                                iInterface.getName());

                SymbolTable.getInstance().saveException(methodNotImplException); //TODO design concern to continue
            }
        }
    }

    private void addInheritedAttributesFromParentClass() {
        if (parentClass != null) {
            IClass parentClass = SymbolTable.getInstance().getClass(this.parentClass.getName());
            for (IVariable v : parentClass.getAttributeMap().values()) {
                IVariable variableWithSameName = attributeMap.get(v.getName());
                if (variableWithSameName != null) {
                    SemanticException methodNotImplException = new SemanticException(variableWithSameName,
                            "atributo con el mismo nombre que el de la clase ancestro " + parentClass.getName());

                    SymbolTable.getInstance().saveException(methodNotImplException); //TODO design concern to continue
                } else {
                    attributeMap.put(v.getName(), v);
                }
            }
        }
    }

    private void validateMethods() {
        for (IMethod m : methodMap.values()) {
            try {
                m.validate(genericType);
            } catch (SemanticException e) {
                SymbolTable.getInstance().saveException(e);
            }
        }
    }

    private void validateAttributes() throws SemanticException {
        for (IVariable v : attributeMap.values()) {
            v.validate(genericType); //TODO design concer to pass generic type in order to checkif class exissts, if corresponding
        }
    }

}
