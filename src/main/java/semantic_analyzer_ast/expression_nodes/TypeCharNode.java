package semantic_analyzer_ast.expression_nodes;

import ceivm.InstructionWriter;
import exceptions.SemanticException;
import semantic_analyzer.IType;
import semantic_analyzer.TypeChar;
import semantic_analyzer_ast.visitors.VisitorExpression;

public class TypeCharNode extends TypeNode {
    public TypeCharNode(String line, int row, int column) {
        super(line, row, column);
    }


    @Override
    public IType getType() {
        return new TypeChar(getLine(), getRow(), getColumn());
    }

    @Override
    public void validate() throws SemanticException {

    }

    @Override
    public void acceptVisitor(VisitorExpression visitorExpression) {
        visitorExpression.visit(this);
    }

    @Override
    public void generateCode() {
        int charAsInt = getToken().getLexeme().charAt(0);
        InstructionWriter.getInstance().write("push", charAsInt);
    }

}
