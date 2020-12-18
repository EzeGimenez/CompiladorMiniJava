package semantic_analyzer_ast.sentence_nodes;

import ceivm.IInstructionWriter;
import ceivm.InstructionWriter;
import ceivm.TagProvider;
import exceptions.SemanticException;
import semantic_analyzer_ast.expression_nodes.ExpressionNode;
import semantic_analyzer_ast.visitors.VisitorIsBoolean;
import semantic_analyzer_ast.visitors.VisitorSentence;

public class WhileNode extends SentenceNode {
    private ExpressionNode condition;
    private SentenceNode body;

    public WhileNode(String line, int row, int column) {
        super(line, row, column);
    }

    @Override
    public void generateCode() {
        IInstructionWriter writer = InstructionWriter.getInstance();
        String whileTag = TagProvider.getWhileTag();
        String whileExitTag = TagProvider.getWhileExitTag();
        writer.addTag(whileTag);
        condition.generateCode();
        writer.write("bf", whileExitTag);
        body.generateCode();
        writer.write("jump", whileTag);
        writer.addTag(whileExitTag);
        writer.write("nop");
    }

    public SentenceNode getBody() {
        return body;
    }

    public void setBody(SentenceNode body) {
        this.body = body;
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public void setCondition(ExpressionNode condition) {
        this.condition = condition;
    }

    @Override
    public void validate() throws SemanticException {
        condition.validate();
        VisitorIsBoolean visitorIsBoolean = new VisitorIsBoolean();
        condition.acceptVisitor(visitorIsBoolean);
        if (!visitorIsBoolean.isBoolean()) {
            throw new SemanticException(this, "debe ser una expresion con tipo boolean");
        }
        body.validate();
    }

    @Override
    public void acceptVisitor(VisitorSentence v) {
        v.visit(this);
    }
}
