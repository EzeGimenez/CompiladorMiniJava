package lexical_analyzer;

import data_structures.Trie;
import data_structures.TrieDescriptor;

import static lexical_analyzer.TokenDescriptor.*;

/**
 *
 */
public class LexicalAnalyzer implements ILexicalAnalyzer {

    private final FileHandler fileHandler;
    private final CharChecker charChecker;
    private final Trie trieDescriptor;

    private String lexeme;
    private int currChar;

    private boolean blockStringFlag;

    public LexicalAnalyzer(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
        this.charChecker = new CharCheckerImpl();
        this.blockStringFlag = false;

        updateCurrChar();

        trieDescriptor = new TrieDescriptor();
        populateKeyWords();
    }

    private void populateKeyWords() {
        trieDescriptor.insert("null", NULL);
        trieDescriptor.insert("true", BOOLEAN);
        trieDescriptor.insert("false", BOOLEAN);
        trieDescriptor.insert("int", PR_INT);
        trieDescriptor.insert("char", PR_CHAR);
        trieDescriptor.insert("boolean", PR_BOOLEAN);
        trieDescriptor.insert("String", PR_STRING);
        trieDescriptor.insert("void", VOID);
        trieDescriptor.insert("class", CLASS);
        trieDescriptor.insert("interface", INTERFACE);
        trieDescriptor.insert("extends", EXTENDS);
        trieDescriptor.insert("static", STATIC);
        trieDescriptor.insert("dynamic", DYNAMIC);
        trieDescriptor.insert("public", PUBLIC);
        trieDescriptor.insert("private", PRIVATE);
        trieDescriptor.insert("if", IF);
        trieDescriptor.insert("else", ELSE);
        trieDescriptor.insert("while", WHILE);
        trieDescriptor.insert("return", RETURN);
        trieDescriptor.insert("this", THIS);
        trieDescriptor.insert("new", NEW);
        trieDescriptor.insert("protected", PROTECTED);
    }

    @Override
    public IToken nextToken() throws LexicalException {
        return e0();
    }

    private void updateLexeme() {
        lexeme = lexeme + ((char) currChar);
    }

    private void updateCurrChar() {
        currChar = fileHandler.nextChar();
    }

    private void removeLexemeDelimiters() {
        StringBuilder stringBuilder = new StringBuilder(lexeme);
        stringBuilder.deleteCharAt(0).deleteCharAt(stringBuilder.length() - 1);
        lexeme = stringBuilder.toString();
    }

    private Token buildTokenFor(TokenDescriptor descriptor) {
        return new Token(descriptor, lexeme, fileHandler.getRow());
    }

    private LexicalException buildLexicalException(String message) {
        lexeme = lexeme.replaceAll("\n", "\\\\n");
        return new LexicalException(
                message,
                fileHandler.getCurrentLine(),
                lexeme,
                fileHandler.getRow(),
                fileHandler.getColumn());
    }

    private IToken e0() throws LexicalException {
        lexeme = "";

        if (blockStringFlag) {
            blockStringFlag = false;
            lexeme = "\"";
            return e8();
        }

        if (charChecker.isEOF(currChar)) {
            return eEOF();
        } else if (charChecker.isWhitespace(currChar)) {
            updateCurrChar();
            return e0();
        } else if (charChecker.isLowerCase(currChar)) {
            updateLexeme();
            updateCurrChar();
            return e1();
        } else if (charChecker.isUpperCase(currChar)) {
            updateLexeme();
            updateCurrChar();
            return e2();
        } else if (charChecker.isDigit(currChar)) {
            updateLexeme();
            updateCurrChar();
            return e3();
        } else if (currChar == '\'') {
            updateLexeme();
            updateCurrChar();
            return e4();
        } else if (currChar == '"') {
            updateLexeme();
            updateCurrChar();
            return e8();
        } else if (currChar == '{') {
            updateLexeme();
            updateCurrChar();
            return e10();
        } else if (currChar == '}') {
            updateLexeme();
            updateCurrChar();
            return e11();
        } else if (currChar == '(') {
            updateLexeme();
            updateCurrChar();
            return e12();
        } else if (currChar == ')') {
            updateLexeme();
            updateCurrChar();
            return e13();
        } else if (currChar == ';') {
            updateLexeme();
            updateCurrChar();
            return e14();
        } else if (currChar == ',') {
            updateLexeme();
            updateCurrChar();
            return e15();
        } else if (currChar == '.') {
            updateLexeme();
            updateCurrChar();
            return e16();
        } else if (currChar == '>') {
            updateLexeme();
            updateCurrChar();
            return e17();
        } else if (currChar == '<') {
            updateLexeme();
            updateCurrChar();
            return e18();
        } else if (currChar == '!') {
            updateLexeme();
            updateCurrChar();
            return e19();
        } else if (currChar == '=') {
            updateLexeme();
            updateCurrChar();
            return e20();
        } else if (currChar == '+') {
            updateLexeme();
            updateCurrChar();
            return e25();
        } else if (currChar == '-') {
            updateLexeme();
            updateCurrChar();
            return e26();
        } else if (currChar == '*') {
            updateLexeme();
            updateCurrChar();
            return e29();
        } else if (currChar == '/') {
            updateLexeme();
            updateCurrChar();
            return e30();
        } else if (currChar == '&') {
            updateLexeme();
            updateCurrChar();
            return e31();
        } else if (currChar == '|') {
            updateLexeme();
            updateCurrChar();
            return e32();
        } else if (currChar == '%') {
            updateLexeme();
            updateCurrChar();
            return e35();
        }

        updateLexeme();
        updateCurrChar();
        throw buildLexicalException("No reconocido");
    }

    private IToken eEOF() {
        return buildTokenFor(END_OF_FILE);
    }

    private IToken e1() {
        if (charChecker.isLetter(currChar) ||
                charChecker.isDigit(currChar) ||
                currChar == '_') {

            updateLexeme();
            updateCurrChar();
            return e1();
        } else {
            TokenDescriptor descriptor = trieDescriptor.search(lexeme);
            if (descriptor == null) {
                descriptor = ID_MET_VAR;
            }
            return buildTokenFor(descriptor);
        }
    }

    private IToken e2() {
        if (charChecker.isLetter(currChar) ||
                charChecker.isDigit(currChar) ||
                currChar == '_') {

            updateLexeme();
            updateCurrChar();
            return e2();
        } else {
            TokenDescriptor descriptor = trieDescriptor.search(lexeme);
            if (descriptor == null) {
                descriptor = ID_CLASS;
            }
            return buildTokenFor(descriptor);
        }
    }

    private IToken e3() {
        if (charChecker.isDigit(currChar)) {
            updateLexeme();
            updateCurrChar();
            return e3();
        } else {
            return buildTokenFor(INTEGER);
        }
    }

    private IToken e4() throws LexicalException {
        if (currChar == '\\') {
            updateLexeme();
            updateCurrChar();
            return e6();
        } else if (currChar == '\'') {
            updateLexeme();
            updateCurrChar();
            throw buildLexicalException("literal de caracter vacio");
        } else if (currChar == '\n') {
            throw buildLexicalException("literal de caracter abierto");
        } else if (charChecker.isEOF(currChar)) {
            throw buildLexicalException("literal de caracter sin cerrar");
        } else {
            updateLexeme();
            updateCurrChar();
            return e5();
        }
    }

    private IToken e5() throws LexicalException {
        if (currChar == '\'') {
            updateLexeme();
            updateCurrChar();
            return e7();
        } else {
            throw buildLexicalException("caracter de escape invalido dentro de literal de caracter");
        }
    }

    private IToken e6() throws LexicalException {
        if (charChecker.isEOF(currChar) || charChecker.isWhitespace(currChar)) {
            throw buildLexicalException("Literal de caracter sin cerrar");
        }
        updateLexeme();
        updateCurrChar();
        return e5();
    }

    private IToken e7() {
        removeLexemeDelimiters();
        return buildTokenFor(CHARACTER);
    }

    private IToken e8() throws LexicalException {
        if (currChar == '"') {
            updateLexeme();
            updateCurrChar();
            return e45();
        } else if (currChar == '\n' || charChecker.isEOF(currChar)) {
            throw buildLexicalException("literal de string sin cerrar");
        } else {
            updateLexeme();
            updateCurrChar();
            return e9();
        }
    }

    private IToken e45() throws LexicalException {
        if (currChar == '"') {
            updateLexeme();
            updateCurrChar();
            return e39();
        }
        return e46();
    }

    private IToken e9() throws LexicalException {
        if (currChar == '"') {
            updateLexeme();
            updateCurrChar();
            return e46();
        } else if (currChar == '\n' || charChecker.isEOF(currChar)) {
            throw buildLexicalException("literal de string sin cerrar");
        }
        updateLexeme();
        updateCurrChar();
        return e9();
    }

    private IToken e46() {
        removeLexemeDelimiters();
        formatStringLexeme();
        return buildTokenFor(STRING);
    }

    private IToken e39() throws LexicalException {
        if (currChar == '\n') {
            updateLexeme();
            updateCurrChar();
            return e40();
        }

        blockStringFlag = true;
        lexeme = "";
        return buildTokenFor(STRING);
    }

    private IToken e40() throws LexicalException {
        if (currChar == '\n') {
            updateLexeme();
            updateCurrChar();
            return e41();
        } else if (charChecker.isEOF(currChar)) {

            throw buildLexicalException("bloque de String sin cerrar");
        } else {
            updateLexeme();
            updateCurrChar();
            return e40();
        }
    }

    private IToken e41() throws LexicalException {
        if (currChar == '"') {
            updateLexeme();
            updateCurrChar();
            return e42();
        } else if (charChecker.isEOF(currChar)) {
            throw buildLexicalException("bloque de String sin cerrar");
        } else if (currChar == '\n') {
            updateLexeme();
            updateCurrChar();
            return e41();
        } else {
            updateLexeme();
            updateCurrChar();
            return e40();
        }
    }

    private IToken e42() throws LexicalException {
        if (currChar == '"') {
            updateLexeme();
            updateCurrChar();
            return e43();
        } else if (charChecker.isEOF(currChar)) {
            throw buildLexicalException("bloque de String sin cerrar");
        } else if (currChar == '\n') {
            updateLexeme();
            updateCurrChar();
            return e41();
        } else {
            updateLexeme();
            updateCurrChar();
            return e40();
        }
    }

    private IToken e43() throws LexicalException {
        if (currChar == '"') {
            updateLexeme();
            updateCurrChar();
            return e44();
        } else if (charChecker.isEOF(currChar)) {
            throw buildLexicalException("bloque de String sin cerrar");
        } else if (currChar == '\n') {
            updateLexeme();
            updateCurrChar();
            return e41();
        } else {
            updateLexeme();
            updateCurrChar();
            return e40();
        }
    }

    private IToken e44() {
        formatStringLexeme();
        removeStringBlockDelimiters();
        return buildTokenFor(STRING);
    }

    private void removeStringBlockDelimiters() {
        StringBuilder stringBuilder = new StringBuilder(lexeme);
        lexeme = stringBuilder.substring(5, lexeme.length() - 5);
    }

    private void formatStringLexeme() {
        lexeme = lexeme.replaceAll("\n", "\\\\n");
    }

    private IToken e10() {
        return buildTokenFor(BRACES_OPEN);
    }

    private IToken e11() {
        return buildTokenFor(BRACES_CLOSE);
    }

    private IToken e12() {
        return buildTokenFor(PARENTHESES_OPEN);
    }

    private IToken e13() {
        return buildTokenFor(PARENTHESES_CLOSE);
    }

    private IToken e14() {
        return buildTokenFor(SEMICOLON);
    }

    private IToken e15() {
        return buildTokenFor(COMMA);
    }

    private IToken e16() {
        return buildTokenFor(DOT);
    }

    private IToken e17() {
        if (currChar == '=') {
            updateLexeme();
            updateCurrChar();
            return e21();
        } else {
            return buildTokenFor(GREATER_THAN);
        }
    }

    private IToken e18() {
        if (currChar == '=') {
            updateLexeme();
            updateCurrChar();
            return e22();
        } else {
            return buildTokenFor(LESS_THAN);
        }
    }

    private IToken e19() {
        if (currChar == '=') {
            updateLexeme();
            updateCurrChar();
            return e24();
        } else {
            return buildTokenFor(OP_NOT);
        }
    }

    private IToken e20() {
        if (currChar == '=') {
            updateLexeme();
            updateCurrChar();
            return e23();
        } else {
            return buildTokenFor(ASSIGN);
        }
    }

    private IToken e21() {
        return buildTokenFor(GREATER_EQUALS);
    }

    private IToken e22() {
        return buildTokenFor(LESS_EQUALS);
    }

    private IToken e23() {
        return buildTokenFor(EQUALS);
    }

    private IToken e24() {
        return buildTokenFor(NOT_EQUALS);
    }

    private IToken e25() {
        if (currChar == '=') {
            updateLexeme();
            updateCurrChar();
            return e27();
        } else {
            return buildTokenFor(ADD);
        }
    }

    private IToken e26() {
        if (currChar == '=') {
            updateLexeme();
            updateCurrChar();
            return e28();
        } else {
            return buildTokenFor(SUB);
        }
    }

    private IToken e27() {
        return buildTokenFor(ASSIGN_ADD);
    }

    private IToken e28() {
        return buildTokenFor(ASSIGN_SUB);
    }

    private IToken e29() {
        return buildTokenFor(MULTIPLY);
    }

    private IToken e30() throws LexicalException {
        if (currChar == '/') {
            updateCurrChar();
            return e36();
        } else if (currChar == '*') {
            updateLexeme();
            updateCurrChar();
            return e37();
        } else {
            return buildTokenFor(DIVIDE);
        }
    }

    private IToken e31() throws LexicalException {
        if (currChar == '&') {
            updateLexeme();
            updateCurrChar();
            return e33();
        }
        throw buildLexicalException("Caracter invalido");
    }

    private IToken e32() throws LexicalException {
        if (currChar == '|') {
            updateLexeme();
            updateCurrChar();
            return e34();
        }
        throw buildLexicalException("Caracter invalido");
    }

    private IToken e33() {
        return buildTokenFor(OP_AND);
    }

    private IToken e34() {
        return buildTokenFor(OP_OR);
    }

    private IToken e35() {
        return buildTokenFor(REMAINDER);
    }

    private IToken e36() throws LexicalException {
        if (currChar == '\n' || charChecker.isEOF(currChar)) {
            updateCurrChar();
            return e0();
        } else {
            updateCurrChar();
            return e36();
        }
    }

    private IToken e37() throws LexicalException {
        if (currChar == '*') {
            updateCurrChar();
            return e38();
        } else if (charChecker.isEOF(currChar)) {
            throw buildLexicalException("comentario de bloque abierto");
        } else {
            updateCurrChar();
            return e37();
        }
    }

    private IToken e38() throws LexicalException {
        if (currChar == '/') {
            updateCurrChar();
            return e0();
        } else {
            updateCurrChar();
            return e37();
        }
    }
}
