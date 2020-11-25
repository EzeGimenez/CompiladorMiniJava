package semantic_analyzer_ast.visitors;

import semantic_analyzer_ast.expression_nodes.*;

public interface VisitorExpression {

    void visit(AccessThisNode accessThisNode);

    void visit(AccessStaticNode accessStaticNode);

    void visit(AccessMethodNode accessMethodNode);

    void visit(AccessVariableNode accessVariableNode);

    void visit(AccessConstructorNode accessConstructorNode);

    void visit(ChainedMethodNode chainedMethodNode);

    void visit(ChainedVariableNode chainedVariableNode);

    void visit(ExpressionBinaryNode expressionBinary);

    void visit(TypeNullNode typeNode);

    void visit(TypeCharNode typeNode);

    void visit(TypeBooleanNode typeNode);

    void visit(TypeIntNode typeNode);

    void visit(TypeStringNode typeNode);
}
