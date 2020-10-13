package lexical_analyzer;

/**
 *
 */
public class LexicalException extends Exception {

    private final String message;
    private final String lineString;
    private final String lexeme;
    private final int line;
    private final int column;

    public LexicalException(String message, String lineString, String lexeme, int line, int column) {
        super(message);
        this.message = message;
        this.lineString = lineString;
        this.lexeme = lexeme;
        this.line = line;
        this.column = column;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getLineString() {
        return lineString;
    }

    public String getLexeme() {
        return lexeme;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }
}
