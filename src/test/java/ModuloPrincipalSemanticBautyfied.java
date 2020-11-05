import exceptions.CompilerException;
import exceptions.SemanticException;
import lexical_analyzer.FileHandler;
import lexical_analyzer.FileHandlerImpl;
import semantic_analyzer.SymbolTable;
import syntax_analyzer.ISyntaxAnalyzer;
import syntax_analyzer.SyntaxAnalyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModuloPrincipalSemanticBautyfied implements ModuloPrincipal {

    private final List<CompilerException> exceptions;

    public ModuloPrincipalSemanticBautyfied(String fileName) throws IOException {
        TestUI userUI = new TestUI();
        exceptions = new ArrayList<>();

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
        SymbolTable.getInstance().consolidate();

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
