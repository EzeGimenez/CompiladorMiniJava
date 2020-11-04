package semantic_analyzer;

public class Parameter extends IParameter {

    private final IType type;

    public Parameter(String name, IType type) {
        this(name, type, "", 0, 0);
    }

    public Parameter(String name, IType type, String line, int row, int column) {
        super(name, line, row, column);
        this.type = type;
    }

    @Override
    public IType getType() {
        return type;
    }

    @Override
    public void compareTo(Object o) throws SemanticException {
        if (o == null || getClass() != o.getClass()) throw new SemanticException(this, "diferentes");
        Parameter parameter = (Parameter) o;
        try {
            type.compareTo(parameter.getType());
        } catch (SemanticException e) {
            throw new SemanticException(e.getEntity(), "distinto tipo de parametro: se encontro " + type.getName() + " donde se esperaba " + parameter.getType().getName());
        }
    }

}
