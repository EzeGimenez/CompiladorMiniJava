package semantic_analyzer_ast.expression_nodes;

import ceivm.IInstructionWriter;
import ceivm.InstructionWriter;
import semantic_analyzer.IType;
import semantic_analyzer.TypeBoolean;
import semantic_analyzer_ast.visitors.VisitorExpression;

public class TypeBooleanNode extends TypeNode {
    public TypeBooleanNode(String line, int row, int column) {
        super(line, row, column);
    }

    @Override
    public IType getType() {
        return new TypeBoolean(getLine(), getRow(), getColumn());
    }

    @Override
    public void acceptVisitor(VisitorExpression visitorExpression) {
        visitorExpression.visit(this);
    }

    @Override
    public void generateCode() {
        IInstructionWriter writer = InstructionWriter.getInstance();
        if (getToken().getLexeme().equals("true")) {
            writer.write("push", 1);
        } else {
            writer.write("push", 0);
        }
    }
}
