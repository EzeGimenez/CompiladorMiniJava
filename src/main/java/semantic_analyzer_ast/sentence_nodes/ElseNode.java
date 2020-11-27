package semantic_analyzer_ast.sentence_nodes;

import exceptions.SemanticException;
import semantic_analyzer_ast.visitors.VisitorEndsInReturn;
import semantic_analyzer_ast.visitors.VisitorSentence;

public class ElseNode extends SentenceNode {

    private SentenceNode sentenceNode;

    public ElseNode(String line, int row, int column) {
        super(line, row, column);
    }

    public SentenceNode getSentenceNode() {
        return sentenceNode;
    }

    public void setBody(SentenceNode sentenceNode) {
        this.sentenceNode = sentenceNode;
    }

    @Override
    public void acceptVisitor(VisitorSentence v) {
        v.visit(this);
    }

    @Override
    public void validate() throws SemanticException {
        if (sentenceNode == null) {
            throw new SemanticException(this, "el cuerpo del else no puede ser vacio");
        }
        sentenceNode.validate();
    }

    public boolean endsInReturn() {
        VisitorEndsInReturn visitorEndsInReturn = new VisitorEndsInReturn();
        sentenceNode.acceptVisitor(visitorEndsInReturn);

        return visitorEndsInReturn.endsInReturn();
    }
}
