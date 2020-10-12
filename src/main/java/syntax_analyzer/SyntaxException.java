package syntax_analyzer;

public class SyntaxException extends Exception {

    private final String lineString;
    private final String expected;
    private final String found;
    private final int line;
    private final int column;

    public SyntaxException(String lineString, String expected, String found, int line, int column) {
        super(expected);
        this.lineString = lineString;
        this.expected = expected;
        this.found = found;
        this.line = line;
        this.column = column;
    }

    public String getLineString() {
        return lineString;
    }

    public String getExpected() {
        return expected;
    }

    public String getFound() {
        return found;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }
}
