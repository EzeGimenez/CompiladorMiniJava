package semantic_analyzer_ast.sentence_nodes;

import exceptions.SemanticException;
import lexical_analyzer.TokenDescriptor;
import semantic_analyzer.IType;
import semantic_analyzer_ast.expression_nodes.ExpressionNode;
import semantic_analyzer_ast.visitors.VisitorIsVariable;
import semantic_analyzer_ast.visitors.VisitorSentence;

public class AssignmentNode extends SentenceNode {

    private ExpressionNode leftSide;
    private ExpressionNode rightSide;

    public AssignmentNode(String line, int row, int column) {
        super(line, row, column);
    }

    @Override
    public void acceptVisitor(VisitorSentence v) {
        v.visit(this);
    }

    public ExpressionNode getLeftSide() {
        return leftSide;
    }

    public void setLeftSide(ExpressionNode leftSide) {
        this.leftSide = leftSide;
    }

    public ExpressionNode getRightSide() {
        return rightSide;
    }

    public void setRightSide(ExpressionNode rightSide) {
        this.rightSide = rightSide;
    }

    @Override
    public void validate() throws SemanticException {
        leftSide.validate();
        VisitorIsVariable visitorIsVariable = new VisitorIsVariable();
        leftSide.acceptVisitor(visitorIsVariable);
        if (!visitorIsVariable.isVariable()) {
            throw new SemanticException(leftSide, "no es una variable");
        }

        rightSide.validate();
        IType leftSideType = leftSide.getType();
        if (!rightSide.getType().acceptTypeChecker(leftSideType.getTypeChecker())) {
            throw new SemanticException(this, "tipos incompatibles");
        }
        if (getToken().getDescriptor().equals(TokenDescriptor.ASSIGN_ADD) ||
                getToken().getDescriptor().equals(TokenDescriptor.ASSIGN_SUB)) {
            if (!leftSideType.getName().equals("int")) {
                throw new SemanticException(this, "+= o -= con tipo distinto a entero");
            }
        }

    }

}