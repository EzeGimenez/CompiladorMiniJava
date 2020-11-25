package semantic_analyzer_ast.expression_nodes;

import exceptions.SemanticException;
import semantic_analyzer.IType;

public abstract class ChainedNode extends ExpressionNode {
    private ChainedNode chainedNode;

    public ChainedNode(String line, int row, int column) {
        super(line, row, column);
    }


    public abstract void validateForAssignemnt(IType prevType) throws SemanticException;

    public abstract void validate(IType prevType) throws SemanticException;

    public abstract IType getType(IType prevType) throws SemanticException;
}
