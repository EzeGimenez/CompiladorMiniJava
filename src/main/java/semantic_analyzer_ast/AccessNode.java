package semantic_analyzer_ast;

import lexical_analyzer.IToken;

public abstract class AccessNode extends Node {
    public AccessNode(IToken token, String line, int row, int column) {
        super(token, line, row, column);
    }
}
