package semantic_analyzer_ast;

import java.util.ArrayList;
import java.util.List;

public class ChainedMethodNode extends ChainedNode {
    private final List<ExpressionNode> actualParameters;

    public ChainedMethodNode(String line, int row, int column) {
        super(line, row, column);
        actualParameters = new ArrayList<>();
    }

    public List<ExpressionNode> getActualParameters() {
        return actualParameters;
    }

    @Override
    public void validate() {

    }
}
