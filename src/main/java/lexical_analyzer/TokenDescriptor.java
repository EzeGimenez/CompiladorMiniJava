package lexical_analyzer;

/**
 * Descriptores del Token
 */
public enum TokenDescriptor {

    ID_MET_VAR,
    ID_CLASS,

    // tipos primitivos
    INTEGER,
    CHARACTER,
    STRING,
    NULL,
    BOOLEAN,

    // palabras reservadas
    //TODO falta agregar true y false
    PR_INT,
    PR_CHAR,
    PR_BOOLEAN,
    PR_STRING,
    TRUE,
    FALSE,
    VOID,
    CLASS,
    INTERFACE,
    EXTENDS,
    IMPLEMENTS,
    STATIC,
    DYNAMIC,
    PUBLIC,
    PRIVATE,
    PROTECTED,
    IF,
    ELSE,
    WHILE,
    RETURN,
    THIS,
    NEW,

    BRACES_OPEN,
    BRACES_CLOSE,
    PARENTHESES_OPEN,
    PARENTHESES_CLOSE,
    SEMICOLON,
    COMMA,
    DOT,

    GREATER_THAN,
    LESS_THAN,
    OP_NOT,
    EQUALS,
    GREATER_EQUALS,
    LESS_EQUALS,
    NOT_EQUALS,
    ADD,
    SUB,
    MULTIPLY,
    DIVIDE,
    OP_AND,
    OP_OR,
    REMAINDER,

    ASSIGN,
    ASSIGN_ADD,
    ASSIGN_SUB,

    END_OF_FILE,

}
