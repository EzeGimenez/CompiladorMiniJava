package semantic_analyzer_ast.expression_nodes;

import exceptions.SemanticException;

public class ExpressionBinaryBetweenIntegers extends ExpressionBinaryBooleanNode {
    public ExpressionBinaryBetweenIntegers(String line, int row, int column) {
        super(line, row, column);
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
