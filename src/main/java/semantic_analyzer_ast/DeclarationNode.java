package semantic_analyzer_ast;

import lexical_analyzer.IToken;

public class DeclarationNode extends Node {
    public DeclarationNode(IToken token, String line, int row, int column) {
        super(token, line, row, column);
    }
}
