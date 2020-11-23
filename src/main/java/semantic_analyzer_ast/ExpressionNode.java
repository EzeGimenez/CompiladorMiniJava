package semantic_analyzer_ast;

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
}
