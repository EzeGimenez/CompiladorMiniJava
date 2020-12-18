import ceivm.CeIASMBuilder;
import ceivm.CeIASMBuilderImpl;
import exceptions.CompilerException;
import exceptions.SemanticException;
import lexical_analyzer.FileHandler;
import lexical_analyzer.FileHandlerImpl;
import semantic_analyzer.SymbolTable;
import syntax_analyzer.ISyntaxAnalyzer;
import syntax_analyzer.SyntaxAnalyzer;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ModuloPrincipalCeIVM implements ModuloPrincipal {

    private final List<CompilerException> exceptions;
    private final UI userUI;

    public ModuloPrincipalCeIVM(String fileName, String fileNameOutput) {
        userUI = new UIConsole();
        exceptions = new ArrayList<>();

        try {
            FileHandler fileHandler = new FileHandlerImpl(fileName);
            ISyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(fileHandler);

            boolean hasLexicalOrSyntaxErrors = analyze(syntaxAnalyzer);
            if (!hasLexicalOrSyntaxErrors) {
                boolean hasSemanticErrors = consolidate();
                if (!hasSemanticErrors) {
                    generateCode(fileNameOutput);
                    System.out.println("[SinErrores]");
                }
            }

            fileHandler.invalidate();
            SymbolTable.invalidate();

        } catch (FileNotFoundException e) {
            userUI.displayError("Archivo no encontrado");
        }
    }

    public List<CompilerException> getCompilerExceptionList() {
        return exceptions;
    }

    private boolean analyze(ISyntaxAnalyzer syntaxAnalyzer) {
        syntaxAnalyzer.start();
        boolean halt = false;
        boolean hasExceptions = false;
        while (!halt) {
            try {
                syntaxAnalyzer.validate();
                halt = true;
            } catch (CompilerException e) {
                hasExceptions = true;
                userUI.displayCompilerError(e);
                exceptions.add(e);
            }
        }
        return hasExceptions;
    }

    private boolean consolidate() {
        boolean halt = false;
        boolean declarationCheckSuccess = SymbolTable.getInstance().declarationCheck();
        boolean hasExceptions = false;
        if (declarationCheckSuccess) {
            SymbolTable.getInstance().sentencesCheck();
        }
        while (!halt) {
            try {
                SymbolTable.getInstance().validate();
                halt = true;
            } catch (SemanticException e) {
                hasExceptions = true;
                userUI.displayCompilerError(e);
                exceptions.add(e);
            }
        }
        return hasExceptions;
    }

    private void generateCode(String fileNameOutput) {
        //TODO file path must be user's input
        CeIASMBuilder ceIASMBuilder = new CeIASMBuilderImpl(fileNameOutput);
        ceIASMBuilder.start();
    }
}
