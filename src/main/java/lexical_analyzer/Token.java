package lexical_analyzer;

public class Token implements IToken {

    private TokenDescriptor descriptor;
    private String lexeme;
    private int lineNumber;

    public Token(TokenDescriptor descriptor, String lexeme, int lineNumber) {
        this.descriptor = descriptor;
        this.lexeme = lexeme;
        this.lineNumber = lineNumber;
    }

    @Override
    public TokenDescriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public String getLexeme() {
        return lexeme;
    }

    @Override
    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public String toString() {
        return "(" +
                descriptor + "," +
                lexeme + "," +
                lineNumber +
                ")";
    }
}
