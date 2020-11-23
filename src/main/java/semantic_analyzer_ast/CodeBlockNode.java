package semantic_analyzer_ast;

import java.util.ArrayList;
import java.util.List;

public class CodeBlockNode extends SentenceNode {

    private final List<SentenceNode> sentences;

    public CodeBlockNode(String line, int row, int column) {
        super(line, row, column);
        sentences = new ArrayList<>();
    }

    @Override
    public void validate() {

    }

    public List<SentenceNode> getSentences() {
        return sentences;
    }
}
