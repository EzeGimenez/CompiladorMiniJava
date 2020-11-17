package semantic_analyzer_ast;

import lexical_analyzer.IToken;

public class ChainedVariableNodeImpl extends ChainedNode {
    public ChainedVariableNodeImpl(IToken token, String line, int row, int column) {
        super(token, line, row, column);
    }
}
