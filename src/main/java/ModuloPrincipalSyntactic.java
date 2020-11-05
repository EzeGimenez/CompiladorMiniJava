import exceptions.SyntaxException;
import lexical_analyzer.FileHandler;
import lexical_analyzer.FileHandlerImpl;
import syntax_analyzer.ISyntaxAnalyzer;
import syntax_analyzer.SyntaxAnalyzer;

import java.io.FileNotFoundException;

public class ModuloPrincipalSyntactic implements ModuloPrincipal {

    private final UI userUI;

    public ModuloPrincipalSyntactic(String fileName) {
        userUI = new UIConsole();
        try {
            FileHandler fileHandler = new FileHandlerImpl(fileName);
            ISyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(fileHandler);

            boolean throwedAnException = analyze(syntaxAnalyzer);
            if (!throwedAnException) {
                System.out.println("[SinErrores]");
            }

            fileHandler.invalidate();
        } catch (FileNotFoundException e) {
            reportFileNotFound(fileName);
        }
    }

    private boolean analyze(ISyntaxAnalyzer syntaxAnalyzer) {
        syntaxAnalyzer.start();
        boolean halt = false;
        boolean hasExceptions = false;
        while (!halt) {
            try {
                syntaxAnalyzer.validate();
                halt = true;
            } catch (SyntaxException e) {
                hasExceptions = true;
                userUI.displayCompilerError(e);
            } catch (Exception e) {
                hasExceptions = true;
                e.printStackTrace();
            }
        }
        return hasExceptions;
    }

    private void reportFileNotFound(String fileName) {
        userUI.displayError("File Not Found: " + fileName);
    }
}
