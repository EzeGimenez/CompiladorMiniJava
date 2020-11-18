package semantic_analyzer_ast;

import lexical_analyzer.IToken;

public class ChainedVariableNode extends ChainedNode {
    private ChainedNode chainedNode;

    public ChainedVariableNode(IToken token, String line, int row, int column) {
        super(token, line, row, column);
    }

    public ChainedNode getChainedNode() {
        return chainedNode;
    }

    public void setChainedNode(ChainedNode chainedNode) {
        this.chainedNode = chainedNode;
    }

    @Override
    public void validate() {

    }
}
