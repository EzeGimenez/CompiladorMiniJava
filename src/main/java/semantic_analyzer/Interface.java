package semantic_analyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Interface extends IInterface {

    private final Map<String, IMethod> methodMap;
    private final Collection<IClassReference> inheritance;

    private IClassReference genericClass;

    public Interface(String name, String line, int row, int column) {
        super(name, line, row, column);
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
    public IClassReference getGenericClass() {
        return genericClass;
    }

    @Override
    public void setGenericClass(IClassReference genericClass) {
        this.genericClass = genericClass;
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
        for (IClassReference c : inheritance) {
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

    private IInterface getInterfaceForReference(IClassReference c) {
        return SymbolTable.getInstance().getInterface(c.getName());
    }

    @Override
    public void consolidate() throws SemanticException {
        inheritanceCheck();
        addInheritedMethods();
        consolidateMethods();
    }

    @Override
    public void compareTo(Object o) throws SemanticException {

    }

    private void inheritanceCheck() throws SemanticException {
        for (IClassReference interfaceRef : inheritance) {
            IInterface parentInterface = getInterfaceForReference(interfaceRef);
            if (parentInterface == null) {
                throw new SemanticException(interfaceRef, "interfaz no definida");
            }
            if (parentInterface.hasAncestor(this.getName())) {
                throw new SemanticException(this, "La interfaz sufre de herencia circular");
            }
            if (parentInterface.getGenericClass() != null) {
                if (interfaceRef.getGenericClass() == null) {
                    throw new SemanticException(interfaceRef, "Falta el tipo generico de interfaz generica");
                }
                if (getGenericClass() == null) {
                    throw new SemanticException(this, "La interfaz hereda de una interfaz generica sin instanciar o declarar su propio tipo generico");
                }
                IClassReference inheritedInterfaceTypeClass = interfaceRef.getGenericClass().getDeepestMismatchClassRef(getGenericClass());
                if (inheritedInterfaceTypeClass != null) {
                    throw new SemanticException(inheritedInterfaceTypeClass, "no definido");
                }
            }
        }
    }

    private void addInheritedMethods() throws SemanticException {
        for (IClassReference c : inheritance) {
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
                    throw new SemanticException(e.getEntity(), "; se intenta cambiar la signatura a un metodo heredado: " + e.getMessage());
                }
            } else {
                methodMap.put(inheritedMethod.getName(), inheritedMethod);
            }
        }
    }

    private void consolidateMethods() {
        for (IMethod m : methodMap.values()) {
            try {
                m.consolidate();
            } catch (SemanticException e) {
                SymbolTable.getInstance().saveException(e);
            }
        }
    }

}
