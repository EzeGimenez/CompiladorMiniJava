package semantic_analyzer_ast;

import lexical_analyzer.IToken;

public abstract class SentenceNode extends Node {
    public SentenceNode(IToken token, String line, int row, int column) {
        super(token, line, row, column);
    }
}
