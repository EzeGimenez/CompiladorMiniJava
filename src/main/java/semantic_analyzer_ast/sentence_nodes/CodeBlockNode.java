package semantic_analyzer_ast.sentence_nodes;

import exceptions.SemanticException;
import semantic_analyzer.SymbolTable;
import semantic_analyzer_ast.visitors.VisitorSentence;

import java.util.ArrayList;
import java.util.List;

public class CodeBlockNode extends SentenceNode {

    private final List<SentenceNode> sentences;
    private final List<DeclarationNode> currentDeclarations;

    public CodeBlockNode(String line, int row, int column) {
        super(line, row, column);
        sentences = new ArrayList<>();
        currentDeclarations = new ArrayList<>();
    }

    public void addDeclaration(DeclarationNode declarationNode) {
        currentDeclarations.add(declarationNode);
    }

    public List<DeclarationNode> getCurrentDeclarations() {
        return currentDeclarations;
    }

    @Override
    public void validate() throws SemanticException {
        for (SentenceNode sentence : sentences) {
            SymbolTable.getInstance().setCurrAST(this);
            try {
                sentence.validate();
            } catch (SemanticException e) {
                SymbolTable.getInstance().saveException(e);
            }
        }

        if (SymbolTable.getInstance().getCurrMethod().getAbstractSyntaxTree() != this) {
            removeDeclarations();
        }
    }

    private void removeDeclarations() {
        SymbolTable
                .getInstance()
                .getCurrMethod()
                .getAbstractSyntaxTree()
                .getCurrentDeclarations()
                .removeAll(currentDeclarations);
    }

    @Override
    public void acceptVisitor(VisitorSentence v) {
        v.visit(this);
    }

    public List<SentenceNode> getSentences() {
        return sentences;
    }
}
