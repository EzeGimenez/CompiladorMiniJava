package semantic_analyzer_ast;

import lexical_analyzer.IToken;

import java.util.ArrayList;
import java.util.List;

public class AccessMethodNode extends AccessNode {
    private final List<ExpressionNode> actualParameters;

    public AccessMethodNode(IToken token, String line, int row, int column) {
        super(token, line, row, column);
        actualParameters = new ArrayList<>();
    }

    @Override
    public void validate() {

    }

    public List<ExpressionNode> getActualParameters() {
        return actualParameters;
    }
}
