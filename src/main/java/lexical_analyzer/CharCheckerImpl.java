package lexical_analyzer;

public class CharCheckerImpl implements CharChecker {

    @Override
    public boolean isUpperCase(int c) {
        int dif = c - 'A';
        return dif >= 0 && dif < 26;
    }

    @Override
    public boolean isLowerCase(int c) {
        int dif = c - 'a';
        return dif >= 0 && dif < 26;
    }

    @Override
    public boolean isEOF(int c) {
        return c == -1;
    }

    @Override
    public boolean isLetter(int c) {
        return isLowerCase(c) || isUpperCase(c);
    }

    @Override
    public boolean isDigit(int c) {
        int dif = c - '0';

        return dif >= 0 && dif < 10;
    }

    @Override
    public boolean isWhitespace(int c) {
        return c == '\n' || c == ' ' || c == '\t';
    }
}
