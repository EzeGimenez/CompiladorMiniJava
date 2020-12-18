package semantic_analyzer_ast.expression_nodes;

import ceivm.InstructionWriter;
import exceptions.SemanticException;
import semantic_analyzer.IType;
import semantic_analyzer.TypeInt;

public class ExpressionUnaryIntegerNode extends ExpressionUnaryNode {
    public ExpressionUnaryIntegerNode(String line, int row, int column) {
        super(line, row, column);
    }

    @Override
    public IType getType() throws SemanticException {
        getOperandNode().validate();
        return new TypeInt(getLine(), getRow(), getColumn());
    }

    @Override
    public void validate() throws SemanticException {
        getOperandNode().validate();
        if (!getOperandNode().getType().getName().equals("int")) {
            throw new SemanticException(this, "operador unario + o - con tipo distinto a int");
        }
    }

    @Override
    public void generateCode() {
        getOperandNode().generateCode();
        if (getToken().getLexeme().equals("-")) {
            InstructionWriter.getInstance().write("neg");
        }
    }
}
