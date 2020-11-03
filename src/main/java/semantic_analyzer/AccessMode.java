package semantic_analyzer;

public class AccessMode extends IAccessMode {

    public AccessMode(String name, String line, int row, int column) {
        super(name, line, row, column);
    }

    @Override
    public void consolidate() throws SemanticException {

    }
}
