package data_structures;

import lexical_analyzer.TokenDescriptor;

public class TrieNodeDescriptor implements TrieNode {

    private static final int ALPHABET_SIZE = 26;
    private TrieNode[] children;
    private TokenDescriptor descriptor;

    public TrieNodeDescriptor() {
        descriptor = null;
        children = new TrieNode[ALPHABET_SIZE * 2];
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            children[i] = null;
        }
    }

    public TrieNode[] getChildren() {
        return children;
    }

    public TokenDescriptor getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(TokenDescriptor descriptor) {
        this.descriptor = descriptor;
    }
}