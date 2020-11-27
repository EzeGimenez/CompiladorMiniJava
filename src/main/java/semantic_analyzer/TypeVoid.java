package semantic_analyzer;

import exceptions.SemanticException;
import semantic_analyzer_ast.type_checker.TypeChecker;
import semantic_analyzer_ast.type_checker.TypePrimitiveChecker;

import java.util.Objects;

public class TypeVoid extends IType {

    public TypeVoid() {
        this("", 0, 0);
    }

    public TypeVoid(String line, int row, int column) {
        super("void", line, row, column);
    }

    @Override
    public void validate(IType genericType) {

    }

    @Override
    public void compareTo(Object o) throws SemanticException {
        if (o == null || getClass() != o.getClass()) throw new SemanticException(this, "diferente tipo");
        if (!Objects.equals(this.getName(), ((TypeVoid) o).getName())) {
            throw new SemanticException(this, "tipos diferentes");
        }
    }

    @Override
    public void validateOverwrite(IClassType ancestorClassRef, IType ancestorType) throws SemanticException {
        if (ancestorType == null || getClass() != ancestorType.getClass()) {
            throw new SemanticException(this, "distinto tipo");
        }
    }

    @Override
    public IType cloneForOverwrite(String line, int row, int column) {
        return new TypeVoid(line, row, column);
    }

    @Override
    public TypeChecker getTypeChecker() {
        return new TypePrimitiveChecker(this);
    }

    @Override
    public boolean acceptTypeChecker(TypeChecker typeChecker) {
        return typeChecker.isCompatible(this);
    }
}
