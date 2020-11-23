package semantic_analyzer_ast;

import semantic_analyzer.IType;

public class AccessStaticNode extends AccessNode {

    private IType classType;

    public AccessStaticNode(String line, int row, int column) {
        super(line, row, column);
    }

    public IType getClassType() {
        return classType;
    }

    public void setClassType(IType classType) {
        this.classType = classType;
    }

    @Override
    public void validate() {

    }

}
