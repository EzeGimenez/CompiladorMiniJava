import lexical_analyzer.LexicalException;
import semantic_analyzer.SemanticException;
import syntax_analyzer.SyntaxException;

/**
 * Abstracción del número de lexema
 */
public interface UI {

    /**
     * mostrar un mensaje informativo al usuario
     *
     * @param message mensaje
     */
    void displayMessage(String message);

    /**
     * mostrar un mensaje de error al usuario
     *
     * @param errorMessage mensaje de error
     */
    void displayError(String errorMessage);

    /**
     * mostrar errorn léxico
     *
     * @param exception excepcion conteniendo identificadores del error
     */
    void displayLexicalError(LexicalException exception);

    /**
     * mostrar error sintactico
     *
     * @param exception excepcion conteniendo identificadores del error
     */
    void displaySyntaxError(SyntaxException exception);

    /**
     * mostrar error semántico
     *
     * @param e excepcion capturada por el analizador
     */
    void displaySemanticError(SemanticException e);
}
