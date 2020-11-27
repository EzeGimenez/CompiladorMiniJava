package semantic_analyzer_ast.visitors;

import semantic_analyzer_ast.sentence_nodes.*;

public class VisitorEndsInReturn implements VisitorSentence {

    private boolean endsInReturn = false;

    public boolean endsInReturn() {
        return endsInReturn;
    }

    @Override
    public void visit(AssignmentNode node) {

    }

    @Override
    public void visit(DeclarationNode node) {

    }

    @Override
    public void visit(CodeBlockNode node) {
        for (SentenceNode s : node.getSentences()) {
            s.acceptVisitor(this);
        }
    }

    @Override
    public void visit(ReturnNode node) {
        endsInReturn = true;
    }

    @Override
    public void visit(EmptySentenceNode node) {

    }

    @Override
    public void visit(AccessSentenceNode node) {

    }

    @Override
    public void visit(IfNode node) {
        endsInReturn = node.endsInReturn();
    }

    @Override
    public void visit(ElseNode node) {
        endsInReturn = node.endsInReturn();
    }

    @Override
    public void visit(WhileNode whileNode) {

    }
}
