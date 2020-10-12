package data_structures;

import lexical_analyzer.TokenDescriptor;

/**
 * Nodo correspondiente al Trie con descriptores
 */
public interface TrieNode {

    /**
     * Obtencion del arreglo conteniendo los hijos del nodo
     * @return arreglo conteniendo a los hijos
     */
    TrieNode[] getChildren();

    /**
     * Retorna el descriptor del nodo si es que contiene, sino null
     * @return el descriptor
     */
    TokenDescriptor getDescriptor();

    /**
     * Añade un descriptor del token a este nodo aceptador
     * @param descriptor el Descriptor del Token
     */
    void setDescriptor(TokenDescriptor descriptor);

}
