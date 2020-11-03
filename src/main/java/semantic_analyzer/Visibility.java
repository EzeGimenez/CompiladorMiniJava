package semantic_analyzer;

public class Visibility extends IVisibility {

    private final String name;
    private final int row, column;

    public Visibility(String name, String line, int row, int column) {
        super(name, line, row, column);
        this.name = name;
        this.row = row;
        this.column = column;
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getColumn() {
        return column;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void consolidate() throws SemanticException {

    }
}
