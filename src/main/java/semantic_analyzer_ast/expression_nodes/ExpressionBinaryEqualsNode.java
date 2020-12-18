package semantic_analyzer_ast.expression_nodes;

import ceivm.IInstructionWriter;
import ceivm.InstructionWriter;
import exceptions.SemanticException;
import semantic_analyzer.IType;
import semantic_analyzer_ast.type_checker.TypeChecker;
import semantic_analyzer_ast.visitors.VisitorExpression;

public class ExpressionBinaryEqualsNode extends ExpressionBinaryBooleanNode {

    public ExpressionBinaryEqualsNode(String line, int row, int column) {
        super(line, row, column);
    }

    @Override
    public void generateCode() {
        getLeftSide().generateCode();
        getRightSide().generateCode();
        IInstructionWriter writer = InstructionWriter.getInstance();

        switch (getToken().getLexeme()) {
            case "==":
                writer.write("eq");
                break;
            case "!=":
                writer.write("ne");
                break;
        }
    }

    @Override
    public void acceptVisitor(VisitorExpression visitorExpression) {
        if (getChainedNode() != null) {
            getChainedNode().acceptVisitor(visitorExpression);
        } else {
            visitorExpression.visit(this);
        }
    }

    @Override
    public void validate() throws SemanticException {
        getLeftSide().validate();
        getRightSide().validate();

        IType leftSideType = getLeftSide().getType();
        IType rightSideType = getRightSide().getType();

        TypeChecker typeCheckerLeft = leftSideType.getTypeChecker();
        TypeChecker typeCheckerRight = rightSideType.getTypeChecker();

        if (!rightSideType.acceptTypeChecker(typeCheckerLeft) &&
                !leftSideType.acceptTypeChecker(typeCheckerRight)) {
            throw new SemanticException(this, "tipos incompatibles");
        }
    }
}
