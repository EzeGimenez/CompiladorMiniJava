package semantic_analyzer_ast.visitors;

import semantic_analyzer_ast.sentence_nodes.*;

public class VisitorDeclarationFinder implements VisitorSentence {
    private final String name;

    private DeclarationNode declarationNodeFound;

    public VisitorDeclarationFinder(String name) {
        declarationNodeFound = null;
        this.name = name;
    }

    public DeclarationNode getDeclarationNodeFound() {
        return declarationNodeFound;
    }

    @Override
    public void visit(AssignmentNode node) {

    }

    @Override
    public void visit(DeclarationNode node) {
        if (node.getToken().getLexeme().equals(name)) {
            declarationNodeFound = node;
        }
    }

    @Override
    public void visit(CodeBlockNode node) {

    }

    @Override
    public void visit(ReturnNode node) {

    }

    @Override
    public void visit(EmptySentenceNode node) {

    }

    @Override
    public void visit(AccessSentenceNode node) {

    }

    @Override
    public void visit(IfNode node) {

    }

    @Override
    public void visit(ElseNode node) {

    }

    @Override
    public void visit(WhileNode whileNode) {

    }
}
