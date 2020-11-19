package semantic_analyzer_ast;

import lexical_analyzer.IToken;

public class TypeBooleanNode extends TypeNode {
    public TypeBooleanNode(IToken token, String line, int row, int column) {
        super(token, line, row, column);
    }
}