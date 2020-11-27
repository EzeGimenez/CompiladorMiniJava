package semantic_analyzer_ast.visitors;

import semantic_analyzer_ast.expression_nodes.*;

public class VisitorIsVariable implements VisitorExpression {

    private boolean isVariable;

    public VisitorIsVariable() {
        isVariable = false;
    }

    public boolean isVariable() {
        return isVariable;
    }

    @Override
    public void visit(AccessThisNode accessThisNode) {

    }

    @Override
    public void visit(AccessStaticNode accessStaticNode) {

    }

    @Override
    public void visit(AccessMethodNode accessMethodNode) {

    }

    @Override
    public void visit(AccessVariableNode accessVariableNode) {
        isVariable = true;
    }

    @Override
    public void visit(AccessConstructorNode accessConstructorNode) {

    }

    @Override
    public void visit(ChainedMethodNode chainedMethodNode) {

    }

    @Override
    public void visit(ChainedVariableNode chainedVariableNode) {
        isVariable = true;
    }

    @Override
    public void visit(ExpressionBinaryBooleanNode expressionBinary) {

    }

    @Override
    public void visit(ExpressionBinaryIntegerNode expressionBinary) {

    }

    @Override
    public void visit(TypeNullNode typeNode) {

    }

    @Override
    public void visit(TypeCharNode typeNode) {

    }

    @Override
    public void visit(TypeBooleanNode typeNode) {

    }

    @Override
    public void visit(TypeIntNode typeNode) {

    }

    @Override
    public void visit(TypeStringNode typeNode) {

    }

    @Override
    public void visit(ExpressionUnaryNode expressionUnaryNode) {

    }
}
