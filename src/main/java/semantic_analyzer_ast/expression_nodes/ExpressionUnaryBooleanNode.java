package semantic_analyzer_ast.expression_nodes;

import ceivm.InstructionWriter;
import exceptions.SemanticException;
import semantic_analyzer.IType;
import semantic_analyzer.TypeBoolean;

public class ExpressionUnaryBooleanNode extends ExpressionUnaryNode {
    public ExpressionUnaryBooleanNode(String line, int row, int column) {
        super(line, row, column);
    }


    @Override
    public IType getType() throws SemanticException {
        getOperandNode().validate();
        return new TypeBoolean(getLine(), getRow(), getColumn());
    }

    @Override
    public void validate() throws SemanticException {
        getOperandNode().validate();
        if (!getOperandNode().getType().getName().equals("boolean")) {
            throw new SemanticException(this, "operador unario ! con tipo distinto a boolean");
        }
    }

    @Override
    public void generateCode() {
        getOperandNode().generateCode();
        if (getToken().getLexeme().equals("-")) {
            InstructionWriter.getInstance().write("not");
        }
    }
}
