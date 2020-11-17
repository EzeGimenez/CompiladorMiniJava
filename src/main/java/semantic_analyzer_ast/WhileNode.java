package semantic_analyzer_ast;

import lexical_analyzer.IToken;

public class WhileNode extends SentenceNode {
    public WhileNode(IToken token, String line, int row, int column) {
        super(token, line, row, column);
    }
}
