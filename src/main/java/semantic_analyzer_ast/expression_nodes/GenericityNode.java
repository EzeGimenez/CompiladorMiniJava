package semantic_analyzer_ast.expression_nodes;

public class GenericityNode extends Node {
    private GenericityNode genericityNode;

    public GenericityNode(String line, int row, int column) {
        super(line, row, column);
    }

    public GenericityNode getGenericityNode() {
        return genericityNode;
    }

    public void setGenericityNode(GenericityNode genericityNode) {
        this.genericityNode = genericityNode;
    }

}
