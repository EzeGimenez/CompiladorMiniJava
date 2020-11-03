package semantic_analyzer;

public abstract class Entity {

    private final int row;
    private final int column;
    private final String name;
    private final String line;

    public Entity(String name, String line, int row, int column) {
        this.row = row;
        this.column = column;
        this.name = name;
        this.line = line;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public String getName() {
        return name;
    }

    public String getLine() {
        return line;
    }

    public abstract void consolidate() throws SemanticException;

    public abstract void compareTo(Object o) throws SemanticException;
}
