package semantic_analyzer_ast;

import lexical_analyzer.IToken;

public class AccessConstructorNode extends AccessNode {
    public AccessConstructorNode(IToken token, String line, int row, int column) {
        super(token, line, row, column);
    }
}
