package semantic_analyzer_ast.type_checker;

import semantic_analyzer.IClassType;
import semantic_analyzer.IType;
import semantic_analyzer.TypePrimitive;
import semantic_analyzer.TypeVoid;

public class TypePrimitiveChecker implements TypeChecker {
    private final IType type;

    public TypePrimitiveChecker(IType type) {
        this.type = type;
    }

    @Override
    public boolean isCompatible(IClassType type) {
        return false;
    }

    @Override
    public boolean isCompatible(TypePrimitive type) {
        return this.type.getName().equals(type.getName());
    }

    @Override
    public boolean isCompatible(TypeVoid typeVoid) {
        return false;
    }
}
