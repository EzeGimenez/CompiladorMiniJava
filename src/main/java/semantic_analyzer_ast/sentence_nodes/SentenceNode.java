package semantic_analyzer_ast.sentence_nodes;

import semantic_analyzer_ast.expression_nodes.Node;
import semantic_analyzer_ast.visitors.VisitorSentence;

public abstract class SentenceNode extends Node {
    public SentenceNode(String line, int row, int column) {
        super(line, row, column);
    }

    public abstract void acceptVisitor(VisitorSentence v);
}
