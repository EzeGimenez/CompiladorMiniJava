package semantic_analyzer_ast.sentence_nodes;

import ceivm.InstructionWriter;
import exceptions.SemanticException;
import semantic_analyzer_ast.visitors.VisitorEndsInReturn;
import semantic_analyzer_ast.visitors.VisitorSentence;

public class ElseNode extends SentenceNode {

    private SentenceNode sentenceNode;
    private String exitTag;

    public ElseNode(String line, int row, int column) {
        super(line, row, column);
    }

    @Override
    public void generateCode() {
        sentenceNode.generateCode();
        InstructionWriter.getInstance().write("jump", exitTag);
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

    public void setExitTag(String exitTag) {
        this.exitTag = exitTag;
    }
}
