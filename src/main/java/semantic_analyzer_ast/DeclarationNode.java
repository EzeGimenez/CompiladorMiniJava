package semantic_analyzer_ast;

import java.util.List;

public class DeclarationNode extends SentenceNode {
    private ExpressionNode accessNode;
    private List<AssignmentNode> declarationAssignments;

    public DeclarationNode(String line, int row, int column) {
        super(line, row, column);
    }

    public List<AssignmentNode> getDeclarationAssignments() {
        return declarationAssignments;
    }

    public void setDeclarationAssignments(List<AssignmentNode> declarationAssignments) {
        this.declarationAssignments = declarationAssignments;
    }

    @Override
    public void validate() {

    }

    public ExpressionNode getAccessNode() {
        return accessNode;
    }

    public void setAccessNode(ExpressionNode accessNode) {
        this.accessNode = accessNode;
    }
}
