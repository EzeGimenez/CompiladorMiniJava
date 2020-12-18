package semantic_analyzer_ast.expression_nodes;

import ceivm.IInstructionWriter;
import ceivm.InstructionWriter;
import exceptions.SemanticException;
import semantic_analyzer.IType;
import semantic_analyzer.TypeInt;
import semantic_analyzer_ast.visitors.VisitorExpression;

public class ExpressionBinaryIntegerNode extends ExpressionBinaryNode {
    public ExpressionBinaryIntegerNode(String line, int row, int column) {
        super(line, row, column);
    }

    @Override
    public void generateCode() {
        getLeftSide().generateCode();
        getRightSide().generateCode();
        IInstructionWriter writer = InstructionWriter.getInstance();

        switch (getToken().getLexeme()) {
            case "+":
                writer.write("add");
                break;
            case "-":
                writer.write("sub");
                break;
            case "*":
                writer.write("mul");
                break;
            case "/":
                writer.write("div");
                break;
            case "%":
                writer.write("mod");
                break;
        }
    }

    @Override
    public IType getType() {
        return new TypeInt(getLine(), getRow(), getColumn());
    }

    @Override
    public void acceptVisitor(VisitorExpression visitorExpression) {
        visitorExpression.visit(this);
    }

    @Override
    public void validate() throws SemanticException {
        getLeftSide().validate();
        getRightSide().validate();
        if (!getLeftSide().getType().getName().equals("int")) {
            throw new SemanticException(this, "operador int con tipo no int");
        }
        if (!getRightSide().getType().getName().equals("int")) {
            throw new SemanticException(this, "operador int con tipo no int");
        }
    }
}
