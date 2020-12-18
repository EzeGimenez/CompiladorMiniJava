package ceivm;

import semantic_analyzer_ast.expression_nodes.*;
import semantic_analyzer_ast.visitors.VisitorExpression;

public class VisitorHasResult implements VisitorExpression {

    private boolean hasResult = false;

    public boolean hasResult() {
        return hasResult;
    }

    @Override
    public void visit(AccessThisNode accessThisNode) {

    }

    @Override
    public void visit(AccessStaticNode accessStaticNode) {

    }

    @Override
    public void visit(AccessMethodNode accessMethodNode) {
        hasResult = !accessMethodNode.getReferencedMethod().getReturnType().getName().equals("void");
    }

    @Override
    public void visit(AccessVariableNode accessVariableNode) {

    }

    @Override
    public void visit(AccessConstructorNode accessConstructorNode) {
        hasResult = true;
    }

    @Override
    public void visit(ChainedMethodNode chainedMethodNode) {
        hasResult = !chainedMethodNode.getReferencedMethod().getReturnType().getName().equals("void");
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
