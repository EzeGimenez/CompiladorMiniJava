import exceptions.CompilerException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UIBeautified implements UI {
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    public void display(String filename, List<CompilerException> exceptions) {
        List<String> fileAsStringList = getFileAsStringList(filename);
        exceptions.sort(new ExceptionComparator());
        int i = 0;
        for (CompilerException e : exceptions) {
            if (e.getRow() > 0 && e.getRow() <= fileAsStringList.size()) {
                //fileAsStringList.add(e.getRow() + i++, ANSI_RED + getColumnError(e) + ANSI_RESET);
                fileAsStringList.add(e.getRow() + i++, getColumnError(e));
            } else {
                //fileAsStringList.add(ANSI_RED + "Error: " + e.getMessage() + ANSI_RESET);
                fileAsStringList.add("Error: " + e.getMessage());
            }
        }

        for (String s : fileAsStringList) {
            System.out.println(s);
        }
    }

    private List<String> getFileAsStringList(String filename) {
        List<String> out = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            int lineNum = 1;

            while ((line = br.readLine()) != null) {
                line = lineNum++ + "\t" + line;
                out.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out;
    }

    private String getColumnError(CompilerException e) {
        String message = "\t" + e.getMessage();
        return getColumnPointer(e.getColumn() - e.getLexeme().length() + 1) + message;
    }

    private String getColumnPointer(int column) {
        String existing_string = "\t";
        StringBuilder builder = new StringBuilder(existing_string);
        for (int i = 0; i < column - 2; i++) {
            builder.append(" ");
        }
        builder.append('^');
        return builder.toString();
    }

    @Override
    public void displayMessage(String message) {

    }

    @Override
    public void displayError(String errorMessage) {
        System.out.println(errorMessage);
    }

    @Override
    public void displayCompilerError(CompilerException exception) {

    }

    private static class ExceptionComparator implements Comparator<CompilerException> {

        @Override
        public int compare(CompilerException o1, CompilerException o2) {
            return o1.getRow() - o2.getRow();
        }
    }
}
