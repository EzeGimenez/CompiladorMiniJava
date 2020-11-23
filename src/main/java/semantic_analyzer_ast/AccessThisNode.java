package semantic_analyzer_ast;

public class AccessThisNode extends AccessNode {
    public AccessThisNode(String line, int row, int column) {
        super(line, row, column);
    }

    @Override
    public void validate() {

    }
}
