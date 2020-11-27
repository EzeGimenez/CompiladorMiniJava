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

public class ModuloPrincipalSemanticBeautified implements ModuloPrincipal {

    private final List<CompilerException> exceptions;

    public ModuloPrincipalSemanticBeautified(String fileName) {
        UIBeautified userUI = new UIBeautified();
        exceptions = new ArrayList<>();

        try {
            FileHandler fileHandler = new FileHandlerImpl(fileName);
            ISyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(fileHandler);

            analyze(syntaxAnalyzer);
            if (exceptions.size() == 0) {
                consolidate();
                if (exceptions.size() == 0) {
                    System.out.println("[SinErrores]");
                }
            }

            userUI.display(fileName, exceptions);
            fileHandler.invalidate();
            SymbolTable.invalidate();

        } catch (FileNotFoundException e) {
            userUI.displayError("Archivo no encontrado");
        }
    }

    public List<CompilerException> getCompilerExceptionList() {
        return exceptions;
    }

    private void analyze(ISyntaxAnalyzer syntaxAnalyzer) {
        syntaxAnalyzer.start();
        boolean halt = false;
        while (!halt) {
            try {
                syntaxAnalyzer.validate();
                halt = true;
            } catch (CompilerException e) {
                exceptions.add(e);
            }
        }
    }

    private void consolidate() {
        boolean halt = false;
        boolean declarationCheckSuccess = SymbolTable.getInstance().declarationCheck();
        if (declarationCheckSuccess) {
            SymbolTable.getInstance().sentencesCheck();
        }
        while (!halt) {
            try {
                SymbolTable.getInstance().validate();
                halt = true;
            } catch (SemanticException e) {
                exceptions.add(e);
            }
        }
    }
}
