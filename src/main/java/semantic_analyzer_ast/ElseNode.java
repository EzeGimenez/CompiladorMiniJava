package semantic_analyzer_ast;

import lexical_analyzer.IToken;

public class ElseNode extends Node {
    public ElseNode(IToken token, String line, int row, int column) {
        super(token, line, row, column);
    }
}
