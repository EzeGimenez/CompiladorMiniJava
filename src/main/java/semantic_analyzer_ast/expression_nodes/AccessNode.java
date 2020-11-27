package semantic_analyzer_ast.expression_nodes;

import exceptions.SemanticException;
import semantic_analyzer.IType;

public abstract class AccessNode extends OperandNode {
    public AccessNode(String line, int row, int column) {
        super(line, row, column);
    }

    public abstract IType getCurrentType() throws SemanticException;

    public void validateChainedNode() throws SemanticException {
        if (getChainedNode() != null) {
            getChainedNode().validate(getCurrentType());
        }
    }
}
