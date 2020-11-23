package semantic_analyzer_ast;

public abstract class SentenceNode extends Node {
    public SentenceNode(String line, int row, int column) {
        super(line, row, column);
    }
}
