package semantic_analyzer;

import exceptions.SemanticException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Interface extends IInterface {

    private final Map<String, IMethod> methodMap;
    private final Collection<IClassType> inheritance;

    private IType genericType;
    private boolean didConsolidate;

    public Interface(String name, String line, int row, int column) {
        super(name, line, row, column);
        methodMap = new HashMap<>();
        inheritance = new ArrayList<>();
        didConsolidate = false;
    }


    @Override
    public void compareTo(Object o) throws SemanticException {

    }

    @Override
    public Collection<IClassType> getInheritance() {
        return inheritance;
    }

    @Override
    public void addInheritance(IClassType iInterface) {
        inheritance.add(iInterface);
    }

    @Override
    public IType getGenericType() {
        return genericType;
    }

    @Override
    public void setGenericType(IClassType genericType) {
        this.genericType = genericType;
    }

    @Override
    public Map<String, IMethod> getMethodMap() {
        return methodMap;
    }

    @Override
    public void addMethod(IMethod method) {
        methodMap.put(method.getName(), method);
    }

    @Override
    public boolean containsMethod(String name) {
        return methodMap.containsKey(name);
    }

    @Override
    protected boolean hasAncestor(String name) {
        if (getName().equals(name)) {
            return true;
        }
        boolean hasAncestor = false;
        for (IClassType c : inheritance) {
            IInterface iInterface = getInterfaceForReference(c);
            if (iInterface != null && iInterface.hasAncestor(name)) {
                hasAncestor = iInterface.hasAncestor(name);
            }
            if (hasAncestor) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void consolidate() throws SemanticException {
        if (!didConsolidate) {
            didConsolidate = true;
            validateInheritance();
            consolidateAncestors();
            addInheritedMethods();
            validateMethods();
        }
    }

    private void consolidateAncestors() throws SemanticException {
        for (IClassType c : inheritance) {
            IInterface iInterface = getInterfaceForReference(c);
            iInterface.consolidate();
        }
    }

    private void validateInheritance() throws SemanticException {
        for (IClassType interfaceRef : inheritance) {
            IInterface parentInterface = getInterfaceForReference(interfaceRef);
            if (parentInterface == null) {
                if (getClassForReference(interfaceRef) != null) {
                    throw new SemanticException(interfaceRef, interfaceRef.getName() + " es una clase");
                }
                throw new SemanticException(interfaceRef, "interfaz no definida");
            }
            interfaceRef.validate(genericType);
            if (parentInterface.hasAncestor(this.getName())) {
                throw new SemanticException(this, "La interfaz sufre de herencia circular");
            }
        }
    }

    private IClass getClassForReference(IClassType c) {
        return SymbolTable.getInstance().getClass(c.getName());
    }

    private IInterface getInterfaceForReference(IClassType c) {
        return SymbolTable.getInstance().getInterface(c.getName());
    }

    private void addInheritedMethods() throws SemanticException {
        for (IClassType c : inheritance) {
            addMethodsFromInterface(c);
        }
    }

    private void addMethodsFromInterface(IClassType interfaceRef) throws SemanticException {
        IMethod methodWithSameName;
        IInterface iInterface = getInterfaceForReference(interfaceRef);
        for (IMethod inheritedMethod : iInterface.getMethodMap().values()) {
            methodWithSameName = methodMap.get(inheritedMethod.getName());
            if (methodWithSameName != null) {
                try {
                    methodWithSameName.validateOverwrite(interfaceRef, inheritedMethod);
                } catch (SemanticException e) {
                    throw new SemanticException(e.getEntity(), "se intenta cambiar la signatura a un metodo heredado: " + e.getMessage());
                }
            } else {
                methodMap.put(inheritedMethod.getName(), inheritedMethod);
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

}
