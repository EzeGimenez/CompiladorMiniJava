package semantic_analyzer_ast.sentence_nodes;

import ceivm.IInstructionWriter;
import ceivm.InstructionWriter;
import exceptions.SemanticException;
import semantic_analyzer.SymbolTable;
import semantic_analyzer_ast.visitors.VisitorEndsInReturn;
import semantic_analyzer_ast.visitors.VisitorSentence;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CodeBlockNode extends SentenceNode {

    private final List<SentenceNode> sentences;
    private final List<DeclarationNode> currentDeclarations;
    private int declarationsCount = 0;

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

    public int getDeclarationsCount() {
        return declarationsCount;
    }

    public void setDeclarationsCount(int declarationsCount) {
        this.declarationsCount = declarationsCount;
    }

    @Override
    public void generateCode() {
        for (SentenceNode s : sentences) {
            SymbolTable.getInstance().setCurrAST(this);
            s.generateCode();
        }

        if (SymbolTable.getInstance().getCurrMethod().getAbstractSyntaxTree() != this) {
            CodeBlockNode mainAST = SymbolTable.getInstance()
                    .getCurrMethod()
                    .getAbstractSyntaxTree();

            mainAST.setDeclarationsCount(mainAST.getDeclarationsCount() - declarationsCount);
        }

        freeMemoryIfNecessary();
    }

    private void freeMemoryIfNecessary() {
        if (declarationsCount > 0) {
            VisitorEndsInReturn visitorEndsInReturn = new VisitorEndsInReturn();
            Iterator<SentenceNode> sentenceNodeIterator = getSentences().iterator();

            SentenceNode currSentence;
            while (sentenceNodeIterator.hasNext()) {
                currSentence = sentenceNodeIterator.next();
                currSentence.acceptVisitor(visitorEndsInReturn);
            }

            if (!visitorEndsInReturn.endsInReturn()) {
                IInstructionWriter writer = InstructionWriter.getInstance();
                writer.write("fmem", declarationsCount);
            }
        }
    }
}
