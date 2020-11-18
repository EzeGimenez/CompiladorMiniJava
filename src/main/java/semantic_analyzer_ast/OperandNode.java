package semantic_analyzer_ast;

import lexical_analyzer.IToken;

public abstract class OperandNode extends ExpressionNode {
    public OperandNode(IToken token, String line, int row, int column) {
        super(token, line, row, column);
    }

}
