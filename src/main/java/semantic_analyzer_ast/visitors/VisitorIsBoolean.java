package semantic_analyzer_ast.visitors;

import semantic_analyzer_ast.expression_nodes.*;

public class VisitorIsBoolean implements VisitorExpression {

    private boolean isBoolean;

    public VisitorIsBoolean() {
        isBoolean = false;
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

    }

    @Override
    public void visit(AccessConstructorNode accessConstructorNode) {

    }

    @Override
    public void visit(ChainedMethodNode chainedMethodNode) {

    }

    @Override
    public void visit(ChainedVariableNode chainedVariableNode) {

    }

    @Override
    public void visit(ExpressionBinaryNode expressionBinary) {

    }

    @Override
    public void visit(TypeNullNode typeNode) {

    }

    @Override
    public void visit(TypeCharNode typeNode) {

    }

    @Override
    public void visit(TypeBooleanNode typeNode) {
        isBoolean = true;
    }

    public boolean isBoolean() {
        return isBoolean;
    }

    @Override
    public void visit(TypeIntNode typeNode) {

    }

    @Override
    public void visit(TypeStringNode typeNode) {

    }
}
