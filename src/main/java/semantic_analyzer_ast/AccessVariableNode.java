package semantic_analyzer_ast;

public class AccessVariableNode extends AccessNode {
    public AccessVariableNode(String line, int row, int column) {
        super(line, row, column);
    }

    @Override
    public void validate() {

    }
}
