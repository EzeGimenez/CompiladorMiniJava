package semantic_analyzer_ast;

import lexical_analyzer.IToken;

public class TypeIntNode extends Node {
    public TypeIntNode(IToken token, String line, int row, int column) {
        super(token, line, row, column);
    }
}
