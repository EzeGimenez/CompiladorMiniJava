import exceptions.CompilerException;

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
     * mostrar error de compilador
     *
     * @param exception excepcion conteniendo identificadores del error
     */
    void displayCompilerError(CompilerException exception);
}
