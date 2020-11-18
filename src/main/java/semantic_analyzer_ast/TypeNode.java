package semantic_analyzer_ast;

import lexical_analyzer.IToken;

public abstract class TypeNode extends Node {
    public TypeNode(IToken token, String line, int row, int column) {
        super(token, line, row, column);
    }

    @Override
    public void validate() {

    }
}
