package semantic_analyzer_ast;

import lexical_analyzer.IToken;

public class TypeStringNode extends TypeNode {
    public TypeStringNode(IToken token, String line, int row, int column) {
        super(token, line, row, column);
    }
}
