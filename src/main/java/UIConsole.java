import exceptions.CompilerException;

public class UIConsole implements UI {

    @Override
    public void displayMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void displayError(String errorMessage) {
        System.out.println(errorMessage);
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
    public void displayCompilerError(CompilerException exception) {
        System.out.println();
        System.out.println("Error: " + exception.getMessage());

        System.out.println("Detalle: " + exception.getRowString());
        String columnPointer = getColumnPointer(exception.getColumn() - exception.getLexeme().length() + 1);

        System.out.println(columnPointer);
        System.out.println("[Error:" + exception.getLexeme() + "|" + exception.getRow() + "]");
        System.out.println();
    }

}
