package semantic_analyzer;

public interface IVariable {

    String getName();

    IType getType();

    IVisibility getVisibility();
}
