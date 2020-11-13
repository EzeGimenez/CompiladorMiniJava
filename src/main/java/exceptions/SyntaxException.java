package exceptions;

public class SyntaxException extends CompilerException {

    private final String lineString;
    private final String expected;
    private final String found;
    private final int row;
    private final int column;

    public SyntaxException(String lineString, String expected, String found, int row, int column) {
        this.lineString = lineString;
        this.expected = expected;
        this.found = found;
        this.row = row;
        this.column = column;
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
        return lineString;
    }

    @Override
    public String getMessage() {
        return "Error sintactico en linea " + row + ": se encontro " + found + " donde se esperaba " + expected;
    }

    @Override
    public String getLexeme() {
        return found;
    }
}
