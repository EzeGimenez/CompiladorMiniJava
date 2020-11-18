package semantic_analyzer_ast;

import lexical_analyzer.IToken;

import java.util.ArrayList;
import java.util.List;

public class CodeBlockNode extends Node {

    private final List<SentenceNode> sentences;

    public CodeBlockNode(IToken token, String line, int row, int column) {
        super(token, line, row, column);
        sentences = new ArrayList<>();
    }

    @Override
    public void validate() {

    }

    public List<SentenceNode> getSentences() {
        return sentences;
    }
}
