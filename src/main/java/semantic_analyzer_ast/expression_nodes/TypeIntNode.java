package semantic_analyzer_ast.expression_nodes;

import semantic_analyzer.IType;

public class TypeIntNode extends TypeNode {
    public TypeIntNode(String line, int row, int column) {
        super(line, row, column);
    }

    @Override
    public IType getType() {
        return null;
    }
}
