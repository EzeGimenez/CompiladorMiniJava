package semantic_analyzer_ast;

import lexical_analyzer.IToken;

public abstract class Node {

    private final IToken token;
    private final String line;
    private final int row;
    private final int column;

    public Node(IToken token, String line, int row, int column) {
        this.token = token;
        this.line = line;
        this.row = row;
        this.column = column;
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
}
