package semantic_analyzer_ast;

import lexical_analyzer.IToken;

public class ExpressionBinaryNode extends Node {

    private Node leftSide, rightSide;
    private IToken token;

    public ExpressionBinaryNode(IToken token, String line, int row, int column) {
        super(token, line, row, column);
    }

    public Node getLeftSide() {
        return leftSide;
    }

    public void setLeftSide(Node leftSide) {
        this.leftSide = leftSide;
    }

    public Node getRightSide() {
        return rightSide;
    }

    public void setRightSide(Node rightSide) {
        this.rightSide = rightSide;
    }

    public IToken getToken() {
        return token;
    }

    public void setToken(IToken token) {
        this.token = token;
    }
}
