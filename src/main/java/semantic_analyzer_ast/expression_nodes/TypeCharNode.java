package semantic_analyzer_ast.expression_nodes;

import exceptions.SemanticException;
import semantic_analyzer.IType;

public class TypeCharNode extends TypeNode {
    public TypeCharNode(String line, int row, int column) {
        super(line, row, column);
    }

    @Override
    public IType getType() {
        return null;
    }

    @Override
    public void validate() throws SemanticException {

    }
}
