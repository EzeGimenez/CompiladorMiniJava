package semantic_analyzer_ast.expression_nodes;

import ceivm.IInstructionWriter;
import ceivm.InstructionWriter;
import exceptions.SemanticException;

public class ExpressionBinaryBetweenBooleans extends ExpressionBinaryBooleanNode {
    public ExpressionBinaryBetweenBooleans(String line, int row, int column) {
        super(line, row, column);
    }

    @Override
    public void generateCode() {
        getLeftSide().generateCode();
        getRightSide().generateCode();
        IInstructionWriter writer = InstructionWriter.getInstance();
        if (getToken().getLexeme().equals("&&")) {
            writer.write("and");
        } else if (getToken().getLexeme().equals("||")) {
            writer.write("or");
        }
    }

    @Override
    public void validate() throws SemanticException {
        getLeftSide().validate();
        getRightSide().validate();
        if (!getLeftSide().getType().getName().equals("boolean")) {
            throw new SemanticException(this, "operador de booleans con tipo no boolean");
        }
        if (!getRightSide().getType().getName().equals("boolean")) {
            throw new SemanticException(this, "operador de booleans con tipo no boolean");
        }
    }
}
