package semantic_analyzer_ast.expression_nodes;

import ceivm.IInstructionWriter;
import ceivm.InstructionWriter;
import exceptions.SemanticException;

public class ExpressionBinaryBetweenIntegers extends ExpressionBinaryBooleanNode {
    public ExpressionBinaryBetweenIntegers(String line, int row, int column) {
        super(line, row, column);
    }

    @Override
    public void generateCode() {
        getLeftSide().generateCode();
        getRightSide().generateCode();
        IInstructionWriter writer = InstructionWriter.getInstance();

        switch (getToken().getLexeme()) {
            case "<":
                writer.write("lt");
                break;
            case ">":
                writer.write("gt");
                break;
            case "<=":
                writer.write("le");
                break;
            case ">=":
                writer.write("ge");
                break;
        }
    }

    @Override
    public void validate() throws SemanticException {
        getLeftSide().validate();
        getRightSide().validate();
        if (!getLeftSide().getType().getName().equals("int")) {
            throw new SemanticException(this, "operador de enteros con tipo no entero");
        }
        if (!getRightSide().getType().getName().equals("int")) {
            throw new SemanticException(this, "operador de enteros con tipo no entero");
        }
    }
}
