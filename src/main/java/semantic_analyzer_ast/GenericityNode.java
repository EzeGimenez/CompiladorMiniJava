package semantic_analyzer_ast;

import lexical_analyzer.IToken;

public class GenericityNode extends Node {
    private GenericityNode genericityNode;

    public GenericityNode(IToken token, String line, int row, int column) {
        super(token, line, row, column);
    }

    public GenericityNode getGenericityNode() {
        return genericityNode;
    }

    public void setGenericityNode(GenericityNode genericityNode) {
        this.genericityNode = genericityNode;
    }

    @Override
    public void validate() {

    }
}
