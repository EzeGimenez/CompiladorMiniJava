package semantic_analyzer_ast.expression_nodes;

import ceivm.InstructionWriter;
import semantic_analyzer.IType;
import semantic_analyzer.TypeClass;
import semantic_analyzer_ast.visitors.VisitorExpression;

public class TypeNullNode extends TypeNode {
    public TypeNullNode(String line, int row, int column) {
        super(line, row, column);
    }

    @Override
    public IType getType() {
        return new TypeClass("null", getLine(), getRow(), getColumn());
    }

    @Override
    public void acceptVisitor(VisitorExpression visitorExpression) {
        visitorExpression.visit(this);
    }

    @Override
    public void generateCode() {
        InstructionWriter.getInstance().write("push", 0);
    }

}
