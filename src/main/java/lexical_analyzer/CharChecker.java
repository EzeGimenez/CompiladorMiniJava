package lexical_analyzer;

/**
 * Interfaz encargada de abstraer el reconocedor de caracteres
 */
public interface CharChecker {

    /**
     * Retorna True en caso de c ser una letra del alfabeto en mayúscula
     * @param c el caracter a verificar
     * @return true en caso que se trate de una letra mayuscula, false en caso contrario
     */
    boolean isUpperCase(int c);

    /**
     * Retorna True en caso de c ser una letra del alfabeto en minuscula
     * @param c el caracter a verificar
     * @return true en caso que se trate de una letra minuscula, false en caso contrario
     */
    boolean isLowerCase(int c);

    /**
     * Verifica si el caracter es EOF
     * @param c entero representando un caracter
     * @return true si el caracter es EOF, falso en caso contrario
     */
    boolean isEOF(int c);

    /**
     * Verifica si un caracter es una letra o no
     * @param c el caracter en representacion entera a verificar
     * @return true si se trata de una Letra del abecedario, falso en caso contrario
     */
    boolean isLetter(int c);

    /**
     * Verifica si un caracter es un número o no
     * @param c el caracter en representacion entera a verificar
     * @return true si se trata de un número, falso en caso contrario
     */
    boolean isDigit(int c);

    /**
     * Verifica si un caracter se trata de un espacio en blanco
     *
     * Ejemplos de espacio en blanco son \n, ' ', \t
     *
     * @param c el caracter a verificar
     * @return True si se trata de un espacio en blanco, falso en caso contrario
     */
    boolean isWhitespace(int c);
}
