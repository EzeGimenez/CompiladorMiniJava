package semantic_analyzer_ast.visitors;

import semantic_analyzer_ast.expression_nodes.*;

public class VisitorIsMethod implements VisitorExpression {

    private boolean isMethod;

    public VisitorIsMethod() {
        isMethod = false;
    }

    public boolean isMethod() {
        return isMethod;
    }

    @Override
    public void visit(AccessThisNode accessThisNode) {

    }

    @Override
    public void visit(AccessStaticNode accessStaticNode) {

    }

    @Override
    public void visit(AccessMethodNode accessMethodNode) {
        isMethod = true;
    }

    @Override
    public void visit(AccessVariableNode accessVariableNode) {

    }

    @Override
    public void visit(AccessConstructorNode accessConstructorNode) {

    }

    @Override
    public void visit(ChainedMethodNode chainedMethodNode) {
        isMethod = true;
    }

    @Override
    public void visit(ChainedVariableNode chainedVariableNode) {

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
