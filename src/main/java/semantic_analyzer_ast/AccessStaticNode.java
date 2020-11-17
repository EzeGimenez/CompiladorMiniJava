package semantic_analyzer_ast;

import lexical_analyzer.IToken;

public class AccessStaticNode extends AccessNode {
    public AccessStaticNode(IToken token, String line, int row, int column) {
        super(token, line, row, column);
    }
}
