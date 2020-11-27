package semantic_analyzer_ast.type_checker;

import semantic_analyzer.*;

public class TypeClassChecker implements TypeChecker {
    private final IClassType type;

    public TypeClassChecker(IClassType type) {
        this.type = type;
    }

    @Override
    public boolean isCompatible(IClassType type) {
        if (type.getName().equals("null")) return true;
        if (this.type.getName().equals(type.getName())) return true;

        IClass classRef = SymbolTable.getInstance().getClass(type.getName());
        IClassType parentRef = classRef.getParentClassRef();

        if (parentRef != null) {
            if (isCompatible(parentRef)) return true;
        }

        for (IClassType iInterface : classRef.getInterfaceInheritanceList()) {
            if (isCompatible(iInterface)) return true;
        }

        IInterface iInterface = SymbolTable.getInstance().getInterface(type.getName());
        if (iInterface != null) {
            for (IClassType parent : iInterface.getInheritance()) {
                if (isCompatible(parent)) return true;
            }
        }

        return false;
    }

    @Override
    public boolean isCompatible(TypePrimitive type) {
        return false;
    }

    @Override
    public boolean isCompatible(TypeVoid typeVoid) {
        return false;
    }
}
