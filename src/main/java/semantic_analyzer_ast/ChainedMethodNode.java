package semantic_analyzer_ast;

import lexical_analyzer.IToken;

import java.util.ArrayList;
import java.util.List;

public class ChainedMethodNode extends ChainedNode {
    private final List<ExpressionNode> actualParameters;
    private ChainedNode chainedNode;

    public ChainedMethodNode(IToken token, String line, int row, int column) {
        super(token, line, row, column);
        actualParameters = new ArrayList<>();
    }

    public ChainedNode getChainedNode() {
        return chainedNode;
    }

    public void setChainedNode(ChainedNode chainedNode) {
        this.chainedNode = chainedNode;
    }

    public List<ExpressionNode> getActualParameters() {
        return actualParameters;
    }

    @Override
    public void validate() {

    }
}
