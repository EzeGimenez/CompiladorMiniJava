package semantic_analyzer_ast.sentence_nodes;

import ceivm.IInstructionWriter;
import ceivm.InstructionWriter;
import exceptions.SemanticException;
import semantic_analyzer.IParameter;
import semantic_analyzer.IType;
import semantic_analyzer.SymbolTable;
import semantic_analyzer_ast.visitors.VisitorDeclarationFinder;
import semantic_analyzer_ast.visitors.VisitorSentence;

import java.util.Iterator;
import java.util.List;

public class DeclarationNode extends SentenceNode {
    private IType type;

    public DeclarationNode(String line, int row, int column) {
        super(line, row, column);
    }

    public IType getType() {
        return type;
    }

    public void setType(IType typeNode) {
        this.type = typeNode;
    }

    @Override
    public void validate() throws SemanticException {
        duplicatesCheck();
        typeCheck();
        SymbolTable.getInstance().getCurrAST().addDeclaration(this);
        if (SymbolTable.getInstance().getCurrMethod().getAbstractSyntaxTree() !=
                SymbolTable.getInstance().getCurrAST()) {
            SymbolTable.getInstance().getCurrMethod().getAbstractSyntaxTree().addDeclaration(this);
        }
    }

    private void typeCheck() throws SemanticException {
        getType().validate(null);
    }

    @Override
    public void acceptVisitor(VisitorSentence v) {
        v.visit(this);
    }

    private void duplicatesCheck() throws SemanticException {
        localDuplicatesCheck();
        parametersDuplicatesCheck();
    }

    private void localDuplicatesCheck() throws SemanticException {
        CodeBlockNode currAST = SymbolTable.getInstance().getCurrMethod().getAbstractSyntaxTree();
        if (currAST.getCurrentDeclarations().size() > 0) {
            Iterator<DeclarationNode> iterator = currAST.getCurrentDeclarations().iterator();
            SentenceNode currentSentence = iterator.next();

            VisitorDeclarationFinder visitorDeclarationFinder = new VisitorDeclarationFinder(getToken().getLexeme());

            while (currentSentence != this) {
                currentSentence.acceptVisitor(visitorDeclarationFinder);

                if (iterator.hasNext()) {
                    currentSentence = iterator.next();
                } else {
                    break;
                }
            }
            if (visitorDeclarationFinder.getDeclarationNodeFound() != null) {
                throw new SemanticException(this, "nombre de variable duplicado");
            }
        }
    }

    private void parametersDuplicatesCheck() throws SemanticException {
        List<IParameter> parametersList = SymbolTable.getInstance().getCurrMethod().getParameterList();
        for (IParameter p : parametersList) {
            if (p.getName().equals(getToken().getLexeme())) {
                throw new SemanticException(this, "nombre de variable duplicado");
            }
        }
    }

    @Override
    public void generateCode() {
        CodeBlockNode currAST = SymbolTable.getInstance().getCurrAST();
        currAST.setDeclarationsCount(currAST.getDeclarationsCount() + 1);

        if (SymbolTable.getInstance().getCurrMethod().getAbstractSyntaxTree() !=
                SymbolTable.getInstance().getCurrAST()) {
            CodeBlockNode mainAST = SymbolTable
                    .getInstance()
                    .getCurrMethod()
                    .getAbstractSyntaxTree();

            mainAST.setDeclarationsCount(mainAST.getDeclarationsCount() + 1);
        }
        IInstructionWriter writer = InstructionWriter.getInstance();
        writer.write("rmem", 1);
    }
}
