package syntax_analyzer;

import exceptions.CompilerException;

public interface ISyntaxAnalyzer {

    void start();

    void validate() throws CompilerException;

}
