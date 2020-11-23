package semantic_analyzer_ast;

import java.util.ArrayList;
import java.util.List;

public class AccessConstructorNode extends AccessNode {
    private final List<ExpressionNode> actualParameters;
    private GenericityNode genericityNode;

    public AccessConstructorNode(String line, int row, int column) {
        super(line, row, column);
        actualParameters = new ArrayList<>();
    }

    @Override
    public void validate() {

    }

    public GenericityNode getGenericityNode() {
        return genericityNode;
    }

    public void setGenericityNode(GenericityNode genericityNode) {
        this.genericityNode = genericityNode;
    }

    public List<ExpressionNode> getActualParameters() {
        return actualParameters;
    }


}
