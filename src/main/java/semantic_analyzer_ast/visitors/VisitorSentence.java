package semantic_analyzer_ast.visitors;

import semantic_analyzer_ast.sentence_nodes.*;

public interface VisitorSentence {

    void visit(AssignmentNode node);

    void visit(DeclarationNode node);

    void visit(CodeBlockNode node);

    void visit(ReturnNode node);

    void visit(EmptySentenceNode node);

    void visit(AccessSentenceNode node);

    void visit(IfNode node);

    void visit(ElseNode node);

    void visit(WhileNode whileNode);
}
