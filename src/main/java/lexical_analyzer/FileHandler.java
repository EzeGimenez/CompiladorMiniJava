package lexical_analyzer;

/**
 * Interfaz en representacion de la abstraccion de un lector de archivo
 */
public interface FileHandler {

    /**
     * obtiene el próximo caracter leido del archivo fuente
     * @return entero en representaicon del caracter leído
     */
    int nextChar();

    /**
     * retorna la linea completa del último caracter leído
     * @return String conteniendo dicha línea
     */
    String getCurrentLine();

    /**
     * Obtiene el número de línea del último caracter leído
     * @return el número de línea indice 1
     */
    int getRow();

    /**
     * Obtiene el número de columna del último caracter leído
     * @return el número de columna indice 1
     */
    int getColumn();

    /**
     * Cierra el FileHandler
     */
    void invalidate();
}
