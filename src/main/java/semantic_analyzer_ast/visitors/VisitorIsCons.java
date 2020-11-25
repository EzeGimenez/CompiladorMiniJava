package semantic_analyzer_ast.visitors;

import semantic_analyzer_ast.expression_nodes.*;

public class VisitorIsCons implements VisitorExpression {

    private boolean isCons;

    public VisitorIsCons() {
        isCons = false;
    }

    public boolean isCons() {
        return isCons;
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
        isCons = true;
    }

    @Override
    public void visit(TypeNode accessConstructorNode) {

    }
}
