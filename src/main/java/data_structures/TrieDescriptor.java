package data_structures;

import lexical_analyzer.TokenDescriptor;

/**
 *
 */
public class TrieDescriptor implements Trie {

    private static final int ALPHABET_SIZE = 26;
    private final TrieNode root;

    public TrieDescriptor() {
        root = new TrieNodeDescriptor();
    }

    @Override
    public void insert(String key, TokenDescriptor descriptor) {
        int length = key.length();
        int correspChildIndex;

        TrieNode currentNode = root;

        for (int offset = 0; offset < length; offset++) {
            correspChildIndex = getIndexForChar(key, offset);

            if (currentNode.getChildren()[correspChildIndex] == null) {
                currentNode.getChildren()[correspChildIndex] = new TrieNodeDescriptor();
            }
            currentNode = currentNode.getChildren()[correspChildIndex];
        }

        currentNode.setDescriptor(descriptor);
    }

    @Override
    public TokenDescriptor search(String key) {
        int length = key.length();
        int correspChildIndex;
        TrieNode currentNode = root;

        for (int offset = 0; offset < length; offset++) {
            correspChildIndex = getIndexForChar(key, offset);

            if (correspChildIndex < 0 || correspChildIndex > ALPHABET_SIZE * 2) {
                return null;
            }
            if (currentNode.getChildren()[correspChildIndex] == null) {
                return null;
            }
            currentNode = currentNode.getChildren()[correspChildIndex];
        }

        return currentNode == null ? null : currentNode.getDescriptor();
    }

    private int getIndexForChar(String key, int offset) {
        int index;
        char currChar = key.charAt(offset);
        if (Character.isUpperCase(currChar)) {
            index = currChar - 'A';
        } else {
            index = currChar - 'a' + 26;
        }
        return index;
    }
}
