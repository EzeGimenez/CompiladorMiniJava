import lexical_analyzer.FileHandler;
import lexical_analyzer.FileHandlerImpl;
import lexical_analyzer.LexicalException;
import semantic_analyzer.SemanticException;
import semantic_analyzer.SymbolTable;
import syntax_analyzer.ISyntaxAnalyzer;
import syntax_analyzer.SyntaxAnalyzer;
import syntax_analyzer.SyntaxException;

import java.io.FileNotFoundException;

public class ModuloPrincipalSemantic implements ModuloPrincipal {

    private final UI userUI;

    public ModuloPrincipalSemantic(String fileName) {
        userUI = new UIConsole();
        try {
            FileHandler fileHandler = new FileHandlerImpl(fileName);
            ISyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(fileHandler);

            boolean hasLexicalOrSyntaxErrors = analyze(syntaxAnalyzer);
            if (!hasLexicalOrSyntaxErrors) {
                boolean hasSemanticErrors = consolidate();
                if (!hasSemanticErrors) {
                    System.out.println("[SinErrores]");
                }
            }

            fileHandler.invalidate();
            SymbolTable.invalidate();
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
                displaySyntaxErrorMesssage(e);
            } catch (LexicalException e) {
                hasExceptions = true;
                displayLexicalErrorMessage(e);
            } catch (SemanticException e) {
                hasExceptions = true;
                displaySemanticErrorMessage(e);
            } catch (Exception e) {
                hasExceptions = true;
                e.printStackTrace();
            }
        }
        return hasExceptions;
    }

    private boolean consolidate() {
        boolean hasExceptions = false, halt = false;
        SymbolTable.getInstance().consolidate();

        while (!halt) {
            try {
                SymbolTable.getInstance().validate();
                halt = true;
            } catch (SemanticException e) {
                hasExceptions = true;
                userUI.displaySemanticError(e);
            }
        }

        return hasExceptions;
    }

    private void displaySemanticErrorMessage(SemanticException e) {
        userUI.displaySemanticError(e);
    }

    private void reportFileNotFound(String fileName) {
        userUI.displayError("File Not Found: " + fileName);
    }

    private void displayLexicalErrorMessage(LexicalException exception) {
        userUI.displayLexicalError(exception);
    }

    private void displaySyntaxErrorMesssage(SyntaxException exception) {
        userUI.displaySyntaxError(exception);
    }
}
