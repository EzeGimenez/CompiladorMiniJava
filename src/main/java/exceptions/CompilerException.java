package exceptions;

public abstract class CompilerException extends Exception {

    public abstract int getRow();

    public abstract int getColumn();

    public abstract String getRowString();

    public abstract String getMessage();

    public abstract String getLexeme();

}
