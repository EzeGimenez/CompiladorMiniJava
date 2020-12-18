package semantic_analyzer_ast.expression_nodes;

import ceivm.IInstructionWriter;
import ceivm.InstructionWriter;
import semantic_analyzer.IType;
import semantic_analyzer.TypeString;
import semantic_analyzer_ast.visitors.VisitorExpression;

public class TypeStringNode extends TypeNode {
    public TypeStringNode(String line, int row, int column) {
        super(line, row, column);
    }

    @Override
    public IType getType() {
        return new TypeString(getLine(), getRow(), getColumn());
    }

    @Override
    public void acceptVisitor(VisitorExpression visitorExpression) {
        visitorExpression.visit(this);
    }

    @Override
    public void generateCode() {
        IInstructionWriter writer = InstructionWriter.getInstance();
        String token = getToken().getLexeme();
        writer.write("rmem", 1, "referencia al string");
        writer.write("push", token.length() + 1, "longitud del string mas el terminador");
        writer.write("push", "simple_malloc");
        writer.write("call");

        for (int i = 0; i <= token.length(); i++) {
            writer.write("dup");

            if (i < token.length()) {
                writer.write("push", token.charAt(i));
            } else {
                writer.write("push", 0);
            }

            writer.write("storeref", i);
        }

    }
}
