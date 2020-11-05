package exceptions;

public class LexicalException extends CompilerException {

    private final String message;
    private final String rowString;
    private final String lexeme;
    private final int row;
    private final int column;

    public LexicalException(String message, String lineString, String lexeme, int row, int column) {
        this.message = message;
        this.rowString = lineString;
        this.lexeme = lexeme;
        this.row = row;
        this.column = column;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getLexeme() {
        return lexeme;
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getColumn() {
        return column;
    }

    @Override
    public String getRowString() {
        return rowString;
    }
}
