package semantic_analyzer_ast.sentence_nodes;

import exceptions.SemanticException;
import semantic_analyzer_ast.visitors.VisitorSentence;

public class EmptySentenceNode extends SentenceNode {

    public EmptySentenceNode(String line, int row, int column) {
        super(line, row, column);
    }

    @Override
    public void generateCode() {

    }

    @Override
    public void acceptVisitor(VisitorSentence v) {
        v.visit(this);
    }

    @Override
    public void validate() throws SemanticException {

    }
}
