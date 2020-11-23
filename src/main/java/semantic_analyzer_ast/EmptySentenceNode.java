package semantic_analyzer_ast;

import lexical_analyzer.IToken;

public class EmptySentenceNode extends SentenceNode {

    public EmptySentenceNode(IToken token, String line, int row, int column) {
        super(line, row, column);
    }

    @Override
    public void validate() {

    }
}
