package semantic_analyzer_ast;

import lexical_analyzer.IToken;

public class TypeClassNode extends TypeNode {
    public TypeClassNode(IToken token, String line, int row, int column) {
        super(token, line, row, column);
    }

    @Override
    public void validate() {

    }
}
