package semantic_analyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Interface extends IInterface {

    private final Map<String, IMethod> methodMap;
    private final Collection<IClassReference> inheritance;

    private IClassReference genericType;
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
    public Collection<IClassReference> getInheritance() {
        return inheritance;
    }

    @Override
    public void addInheritance(IClassReference iInterface) {
        inheritance.add(iInterface);
    }

    @Override
    public IClassReference getGenericType() {
        return genericType;
    }

    @Override
    public void setGenericType(IClassReference genericType) {
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
        for (IClassReference c : inheritance) {
            IInterface iInterface = getInterfaceForReference(c);
            iInterface.consolidate();
        }
    }

    private void validateInheritance() throws SemanticException {
        for (IClassReference interfaceRef : inheritance) {
            IInterface parentInterface = getInterfaceForReference(interfaceRef);
            if (parentInterface == null) {
                if (getClassForReference(interfaceRef) != null) {
                    throw new SemanticException(interfaceRef, "Se intenta extender desde una interfaz la clase " + interfaceRef.getName());
                }
                throw new SemanticException(interfaceRef, "interfaz no definida");
            }
            if (parentInterface.hasAncestor(this.getName())) {
                throw new SemanticException(this, "La interfaz sufre de herencia circular");
            }
            if (parentInterface.getGenericType() != null) {
                if (interfaceRef.getGenericClass() == null) {
                    throw new SemanticException(interfaceRef, "Falta el tipo generico de interfaz generica");
                }
                if (getGenericType() == null) {
                    throw new SemanticException(this, "La interfaz hereda de una interfaz generica sin instanciar o declarar su propio tipo generico");
                }
                interfaceRef.getGenericClass().validate(getGenericType());
            }
        }
    }

    private IInterface getInterfaceForReference(IClassReference c) {
        return SymbolTable.getInstance().getInterface(c.getName());
    }

    private IClass getClassForReference(IClassReference c) {
        return SymbolTable.getInstance().getClass(c.getName());
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
