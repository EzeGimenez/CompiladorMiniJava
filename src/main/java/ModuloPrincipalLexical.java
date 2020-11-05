import exceptions.LexicalException;
import lexical_analyzer.*;

import java.io.FileNotFoundException;

import static lexical_analyzer.TokenDescriptor.EOF;


public class ModuloPrincipalLexical implements ModuloPrincipal {

    private final UI userUI;

    public ModuloPrincipalLexical(String fileName) {
        userUI = new UIConsole();
        try {
            FileHandler fileHandler = new FileHandlerImpl(fileName);
            askForTokens(fileHandler);
            fileHandler.invalidate();

        } catch (FileNotFoundException e) {
            reportFileNotFound(fileName);
        }
    }

    private void askForTokens(FileHandler fileHandler) {
        ILexicalAnalyzer ILexicalAnalyzer = new LexicalAnalyzer(fileHandler);

        IToken currentToken = null;
        while (currentToken == null || !currentToken.getDescriptor().equals(EOF)) {
            try {
                currentToken = ILexicalAnalyzer.nextToken();
                userUI.displayMessage(currentToken.toString());
            } catch (LexicalException e) {
                userUI.displayCompilerError(e);
            }
        }
    }

    private void reportFileNotFound(String fileName) {
        userUI.displayError("File Not Found: " + fileName);
    }
}
