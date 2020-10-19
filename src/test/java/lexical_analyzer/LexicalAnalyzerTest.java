package lexical_analyzer;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import static lexical_analyzer.TokenDescriptor.EOF;
import static org.junit.Assert.assertEquals;

public class LexicalAnalyzerTest {

    @Test
    public void nextToken() throws IOException {
        FileHandler fileHandler = new FileHandlerImpl("test.java");
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(fileHandler);

        compare(lexicalAnalyzer);
    }

    private void compare(LexicalAnalyzer lexicalAnalyzer) throws IOException {
        Reader reader = new FileReader("expected.txt");
        BufferedReader bufferTest = new BufferedReader(reader);

        String line = null;
        IToken currentToken = null;
        while (currentToken == null || !currentToken.getDescriptor().equals(EOF)) {
            try {
                line = bufferTest.readLine();
                currentToken = lexicalAnalyzer.nextToken();

                assertEquals(currentToken.toString(), line);
            } catch (LexicalException e) {
                assertEquals(e.getMessage().split(",")[1], line);
            }
        }
    }

}