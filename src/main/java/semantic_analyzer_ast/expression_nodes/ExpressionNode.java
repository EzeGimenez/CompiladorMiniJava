package semantic_analyzer_ast.expression_nodes;

import exceptions.SemanticException;
import semantic_analyzer.IType;
import semantic_analyzer_ast.visitors.VisitorExpression;

public abstract class ExpressionNode extends Node {
    private ChainedNode chainedNode;

    public ExpressionNode(String line, int row, int column) {
        super(line, row, column);
    }

    public ChainedNode getChainedNode() {
        return chainedNode;
    }

    public void setChainedNode(ChainedNode chainedNode) {
        this.chainedNode = chainedNode;
    }

    public abstract IType getType() throws SemanticException;

    public abstract void acceptVisitor(VisitorExpression visitorExpression);

    public abstract void validate() throws SemanticException;
}
