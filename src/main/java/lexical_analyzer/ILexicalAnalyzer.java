package lexical_analyzer;

/**
 * Interfaz en representacion de la abstraccion del módulo de Analizador Léxico
 */
public interface ILexicalAnalyzer {

    /**
     * Obtiene el próximo token léxico identificado del archivo fuente
     *
     * @return IToken con el lexema y el descriptor del token
     * @throws LexicalException si se detecta un error léxico al intentar leer el próximo token
     */
    IToken nextToken() throws LexicalException;

}
