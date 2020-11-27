package semantic_analyzer_ast.expression_nodes;

import exceptions.SemanticException;
import semantic_analyzer.IType;
import semantic_analyzer_ast.visitors.VisitorExpression;

public abstract class ChainedNode extends Node {
    private ChainedNode chainedNode;

    public ChainedNode(String line, int row, int column) {
        super(line, row, column);
    }

    public abstract void acceptVisitor(VisitorExpression visitorExpression);

    public ChainedNode getChainedNode() {
        return chainedNode;
    }

    public void setChainedNode(ChainedNode chainedNode) {
        this.chainedNode = chainedNode;
    }

    public abstract void validate(IType prevType) throws SemanticException;

    public abstract IType getType(IType prevType) throws SemanticException;

    public abstract void validateStatic(IType currentType) throws SemanticException;
}
