package semantic_analyzer_ast;

import lexical_analyzer.IToken;

public abstract class Node {

    private IToken token;
    private final String line;
    private final int row;
    private final int column;

    public Node(String line, int row, int column) {
        this.line = line;
        this.row = row;
        this.column = column;
    }

    public void setToken(IToken token) {
        this.token = token;
    }

    public IToken getToken() {
        return token;
    }

    public String getLine() {
        return line;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public abstract void validate();
}
