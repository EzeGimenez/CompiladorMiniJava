package semantic_analyzer_ast;

import lexical_analyzer.IToken;

public class AccessStaticNode extends AccessNode {

    private ChainedNode chainedNode;

    public AccessStaticNode(IToken token, String line, int row, int column) {
        super(token, line, row, column);
    }

    @Override
    public void validate() {

    }

    public ChainedNode getChainedNode() {
        return chainedNode;
    }

    public void setChainedNode(ChainedNode chainedNode) {
        this.chainedNode = chainedNode;
    }

}