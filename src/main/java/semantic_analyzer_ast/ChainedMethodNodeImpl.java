package semantic_analyzer_ast;

import lexical_analyzer.IToken;

public class ChainedMethodNodeImpl extends ChainedNode {
    public ChainedMethodNodeImpl(IToken token, String line, int row, int column) {
        super(token, line, row, column);
    }
}
