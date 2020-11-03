package lexical_analyzer;

public class Token implements IToken {

    private final TokenDescriptor descriptor;
    private final String lexeme;
    private final int lineNumber;

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
