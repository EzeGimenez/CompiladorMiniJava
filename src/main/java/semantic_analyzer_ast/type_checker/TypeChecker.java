package semantic_analyzer_ast.type_checker;

import semantic_analyzer.IClassType;
import semantic_analyzer.TypePrimitive;
import semantic_analyzer.TypeVoid;

public interface TypeChecker {

    boolean isCompatible(IClassType type);

    boolean isCompatible(TypePrimitive type);

    boolean isCompatible(TypeVoid typeVoid);
}
