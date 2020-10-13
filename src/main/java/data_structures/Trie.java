package data_structures;

import lexical_analyzer.TokenDescriptor;

/**
 * Interfaz de Trie con descriptor para identificar los nodos aceptadores de palabras claves
 *
 * Cada Nodo posee un arreglo conteniendo en orden alfabético (si es que hay) el nodo hijo en representacion de la letra.
 */
public interface Trie {

    /**
     * Inserta una llave en el arbol Trie y en su nodo final contiene el descritpor
     *  @param key        la llave a insertar
     * @param descriptor el descriptor de la palabra clave
     */
    void insert(String key, TokenDescriptor descriptor);

    /**
     * Busca en el arbol Trie la llave ingresada
     *
     * @param key llave a buscar
     * @return el descriptor de el token que representa la palabra clave encontrada, si la llave no
     * es encontrada, returna null
     */
    TokenDescriptor search(String key);

}
