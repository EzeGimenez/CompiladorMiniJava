package semantic_analyzer;

import exceptions.SemanticException;
import semantic_analyzer_ast.sentence_nodes.SentenceNode;
import semantic_analyzer_ast.visitors.VisitorEndsInReturn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Constructor extends IMethod {
    private final List<IParameter> parameterList;
    private final IType returnType;

    public Constructor(String name, IType returnType, String line, int row, int column) {
        super(name, line, row, column);
        this.returnType = returnType;
        parameterList = new ArrayList<>();
    }

    public Constructor(String name, IType returnType) {
        this(name, returnType, "", 0, 0);
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
        return null;
    }

    public void validate(IType genericType) throws SemanticException {
        for (IParameter p : getParameterList()) {
            p.getType().validate(genericType);
        }
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
        IMethod out = new Constructor(
                getName(),
                returnType.cloneForOverwrite(line, row, column),
                line,
                row,
                column
        );
        out.setAbstractSyntaxTree(getAbstractSyntaxTree());
        for (IParameter p : parameterList) {
            out.addParameter(p.cloneForOverWrite(line, row, column));
        }
        return out;
    }

    @Override
    public List<IParameter> getParameterList() {
        return parameterList;
    }

    @Override
    public void sentencesCheck() throws SemanticException {
        super.sentencesCheck();
        findReturnStatement();
    }

    private void findReturnStatement() throws SemanticException {

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

    }
}
