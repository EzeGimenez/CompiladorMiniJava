package semantic_analyzer;

import ceivm.IInstructionWriter;
import ceivm.InstructionWriter;
import exceptions.SemanticException;
import semantic_analyzer_ast.sentence_nodes.SentenceNode;
import semantic_analyzer_ast.visitors.VisitorEndsInReturn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Method extends IMethod {

    private final List<IParameter> parameterList;
    private final IType returnType;
    private final IAccessMode accessMode;

    public Method(IAccessMode accessMode, IType returnType, String name) {
        this(accessMode, returnType, name, "", 0, 0);
    }

    public Method(IAccessMode accessMode, IType returnType, String name, String line, int row, int column) {
        super(name, line, row, column);
        this.accessMode = accessMode;
        this.returnType = returnType;

        parameterList = new ArrayList<>();
    }

    @Override
    public List<IParameter> getParameterList() {
        return parameterList;
    }

    @Override
    public void addParameter(IParameter parameter) {
        parameterList.add(parameter);
    }

    @Override
    public IType getReturnType() {
        return returnType;
    }

    @Override
    public IAccessMode getAccessMode() {
        return accessMode;
    }

    @Override
    public boolean containsParameter(String parameterName) {
        for (IParameter p : parameterList) {
            if (Objects.equals(p.getName(), parameterName)) return true;
        }
        return false;
    }

    @Override
    public IParameter getParameter(String parameterName) {
        for (IParameter p : parameterList) {
            if (Objects.equals(p.getName(), parameterName)) return p;
        }
        return null;
    }

    @Override
    public IMethod cloneForOverwrite(String line, int row, int column) {
        IMethod out = new Method(
                accessMode.cloneForOverwrite(row, column),
                returnType.cloneForOverwrite(line, row, column),
                getName(),
                getLine(),
                row,
                column
        );
        out.setTag(getTag());
        out.setOffset(getOffset());
        out.setAbstractSyntaxTree(getAbstractSyntaxTree());
        for (IParameter p : parameterList) {
            out.addParameter(p.cloneForOverWrite(line, row, column));
        }
        return out;
    }

    @Override
    public void sentencesCheck() throws SemanticException {
        super.sentencesCheck();
        findReturnStatement();
    }

    private void findReturnStatement() throws SemanticException {
        if (!getReturnType().getName().equals("void")) {

            VisitorEndsInReturn visitorEndsInReturn = new VisitorEndsInReturn();
            Iterator<SentenceNode> sentenceNodeIterator = getAbstractSyntaxTree().getSentences().iterator();

            SentenceNode currSentence = null;
            if (sentenceNodeIterator.hasNext()) {
                currSentence = sentenceNodeIterator.next();
            }

            while (currSentence != null) {
                currSentence.acceptVisitor(visitorEndsInReturn);

                if (sentenceNodeIterator.hasNext()) {
                    currSentence = sentenceNodeIterator.next();
                    if (visitorEndsInReturn.endsInReturn()) {
                        throw new SemanticException(currSentence, "codigo muerto desde aca");
                    }
                } else {
                    currSentence = null;
                }
            }

            if (!visitorEndsInReturn.endsInReturn()) {
                throw new SemanticException(this, "falta una sentencia de retorno");
            }
        }
    }

    public void generateCode() {
        IInstructionWriter writer = InstructionWriter.getInstance();
        writer.write("loadfp");
        writer.write("loadsp");
        writer.write("storefp");

        getAbstractSyntaxTree().generateCode();

        addReturnInstruction();
    }

    private void addReturnInstruction() {
        VisitorEndsInReturn visitorEndsInReturn = new VisitorEndsInReturn();
        Iterator<SentenceNode> sentenceNodeIterator = getAbstractSyntaxTree().getSentences().iterator();

        SentenceNode currSentence;
        while (sentenceNodeIterator.hasNext()) {
            currSentence = sentenceNodeIterator.next();
            currSentence.acceptVisitor(visitorEndsInReturn);
        }
        IInstructionWriter writer = InstructionWriter.getInstance();
        if (!visitorEndsInReturn.endsInReturn()) {
            writer.write("storefp");

            writer.write("ret", parameterList.size() + 1);
        } else {
            writer.write("nop");
        }
    }

}
