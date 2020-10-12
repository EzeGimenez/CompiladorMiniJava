import lexical_analyzer.LexicalException;
import syntax_analyzer.SyntaxException;

public class UIConsole implements UI {

    @Override
    public void displayMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void displayError(String errorMessage) {
        System.out.println(errorMessage);
    }

    @Override
    public void displayLexicalError(LexicalException e) {

        System.out.println();
        System.out.println("Error lexico en linea " + e.getLine() + ": " + e.getLexeme() + " " + e.getMessage());

        System.out.println("Detalle: " + e.getLineString());
        String columnPointer = getColumnPointer(e.getColumn() - e.getLexeme().length() + 1);

        System.out.println(columnPointer);
        System.out.println("[Error:" + e.getLexeme() + "|" + e.getLine() + "]");
        System.out.println();
    }

    private String getColumnPointer(int column) {
        String existing_string = "       ";
        StringBuilder builder = new StringBuilder(existing_string);
        for (int i = 0; i < column; i++) {
            builder.append(" ");
        }
        builder.append('^');
        return builder.toString();
    }

    @Override
    public void displaySyntaxError(SyntaxException e) {
        System.out.println();
        System.out.println("Error sintactico en linea " + e.getLine() + ": se encontró " + e.getFound() + " donde se esperaba " + e.getExpected());

        System.out.println("Detalle: " + e.getLineString());
        String columnPointer = getColumnPointer(e.getColumn() - e.getFound().length() + 1);

        System.out.println(columnPointer);
        System.out.println("[Error:" + e.getFound() + "|" + e.getLine() + "]");
        System.out.println();
    }

}
