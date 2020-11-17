package semantic_analyzer_ast;

import lexical_analyzer.IToken;

public class ReturnNode extends SentenceNode {
    public ReturnNode(IToken token, String line, int row, int column) {
        super(token, line, row, column);
    }
}
