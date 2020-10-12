package lexical_analyzer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandlerImpl implements FileHandler {

    private Reader buffer;

    private List<Integer> currentLine;
    private int line;
    private int column;

    public FileHandlerImpl(String filename) throws FileNotFoundException {

        InputStream inputStream = new FileInputStream(filename);
        Reader reader = new InputStreamReader(inputStream);
        buffer = new BufferedReader(reader);

        line = 0;
        currentLine = new ArrayList<>();
        fetchNewLine();
    }

    @Override
    public int nextChar() {

        if (column >= currentLine.size()) {
            fetchNewLine();
        }

        return currentLine.get(column++);
    }

    private void fetchNewLine() {
        try {
            currentLine.clear();
            int currChar;
            boolean halt = false;
            while (!halt) {
                currChar = buffer.read();

                if (currChar != '\r') {
                    if (currChar == '\t') {
                        currentLine.add((int) ' ');
                        currentLine.add((int) ' ');
                        currentLine.add((int) ' ');
                        currentLine.add((int) ' ');
                    } else {
                        currentLine.add(currChar);
                    }
                    if (lineEnded(currChar)) {
                        halt = true;
                    }
                }
            }
            column = 0;
            line++;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean lineEnded(int currChar) {
        return currChar == '\n' || currChar == -1;
    }

    @Override
    public String getCurrentLine() {
        StringBuilder builder = new StringBuilder();
        for (int c : currentLine) {
            if (c != '\n' && c != -1) {
                builder.append((char) c);
            }
        }
        return builder.toString();
    }

    @Override
    public int getRow() {
        return line;
    }

    @Override
    public int getColumn() {
        return column;
    }

    @Override
    public void invalidate() {
        try {
            buffer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
