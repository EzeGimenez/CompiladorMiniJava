package semantic_analyzer_ast;

import lexical_analyzer.IToken;

public abstract class ExpressionNode extends Node {
    public ExpressionNode(IToken token, String line, int row, int column) {
        super(token, line, row, column);
    }
}
