package lexical_analyzer;

/**
 * Abstracción de un Token, contiene además los descriptores de los Tokens
 */
public interface IToken {

    /**
     * Obtiene el descriptor del token que representa
     *
     * @return el descriptor
     */
    TokenDescriptor getDescriptor();

    /**
     * Obtiene el lexema del Token
     *
     * @return String conteniendo el lexema
     */
    String getLexeme();

    /**
     * Obtiene el numero de línea en donde se encontro el Token
     *
     * @return entero con el numero de linea indice 1
     */
    int getLineNumber();

}
