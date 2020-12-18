package semantic_analyzer_ast.sentence_nodes;

import ceivm.InstructionWriter;
import ceivm.VisitorHasResult;
import exceptions.SemanticException;
import semantic_analyzer_ast.expression_nodes.ExpressionNode;
import semantic_analyzer_ast.visitors.VisitorIsCons;
import semantic_analyzer_ast.visitors.VisitorIsMethod;
import semantic_analyzer_ast.visitors.VisitorSentence;

public class AccessSentenceNode extends SentenceNode {
    private ExpressionNode expressionNode;

    public AccessSentenceNode(String currentLine, int row, int column) {
        super(currentLine, row, column);
    }

    @Override
    public void acceptVisitor(VisitorSentence v) {
        v.visit(this);
    }

    public ExpressionNode getExpressionNode() {
        return expressionNode;
    }

    public void setExpressionNode(ExpressionNode expressionNode) {
        this.expressionNode = expressionNode;
    }

    @Override
    public void validate() throws SemanticException {
        expressionNode.validate();
        VisitorIsMethod visitorIsMethod = new VisitorIsMethod();
        expressionNode.acceptVisitor(visitorIsMethod);
        if (expressionNode.getChainedNode() == null) {
            VisitorIsCons visitorIsCons = new VisitorIsCons();
            expressionNode.acceptVisitor(visitorIsCons);
            if (!visitorIsMethod.isMethod() && !visitorIsCons.isCons()) {
                throw new SemanticException(expressionNode, "no es una llamada o constructor");
            }
        } else {
            if (!visitorIsMethod.isMethod()) {
                throw new SemanticException(expressionNode, "no es una llamada");
            }
        }
    }

    @Override
    public void generateCode() {
        expressionNode.generateCode();

        VisitorHasResult visitorHasResult = new VisitorHasResult();
        expressionNode.acceptVisitor(visitorHasResult);
        boolean isResultIgnored = visitorHasResult.hasResult();
        if (isResultIgnored) {
            InstructionWriter.getInstance().write("pop", null, "el resultado anterior es ignorado");
        }
    }

}
