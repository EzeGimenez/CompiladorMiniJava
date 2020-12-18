package semantic_analyzer_ast.expression_nodes;

import exceptions.SemanticException;

public abstract class TypeNode extends OperandNode {
    public TypeNode(String line, int row, int column) {
        super(line, row, column);
    }

    @Override
    public void validate() throws SemanticException {

    }
}
