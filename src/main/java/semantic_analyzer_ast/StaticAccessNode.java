package semantic_analyzer_ast;

import lexical_analyzer.IToken;

public class StaticAccessNode extends Node {
    public StaticAccessNode(IToken token, String line, int row, int column) {
        super(token, line, row, column);
    }
}
