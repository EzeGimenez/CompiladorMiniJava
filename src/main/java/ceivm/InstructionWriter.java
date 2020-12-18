package ceivm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class InstructionWriter implements IInstructionWriter {

    private static final String DEFAULT_FILE_PATH = "code";
    private static IInstructionWriter instance;
    private FileWriter fileWriter;

    public static IInstructionWriter getInstance() {
        if (instance == null) {
            instance = new InstructionWriter();
        }
        return instance;
    }

    @Override
    public void setFilePath(String filePath) {
        try {
            File file = new File(filePath);
            fileWriter = new FileWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(String instruction, String argument, String comment) {
        if (fileWriter == null) {
            setFilePath(DEFAULT_FILE_PATH);
        }
        StringBuilder stringBuilder = new StringBuilder("\t" + instruction.toUpperCase());
        if (argument != null) {
            stringBuilder
                    .append(" ")
                    .append(argument);
        }

        if (comment != null) {
            stringBuilder
                    .append("\t\t\t\t;")
                    .append(comment);
        }

        stringBuilder.append("\n");

        //nullPointerCheck(instruction);
        try {
            fileWriter.write(stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(String instruction, int argument, String comment) {
        write(instruction, String.valueOf(argument), comment);
    }

    @Override
    public void write(String instruction, String argument) {
        write(instruction, argument, null);
    }

    @Override
    public void write(String instruction, int argument) {
        write(instruction, argument, null);
    }

    @Override
    public void write(String instruction) {
        write(instruction, null, null);
    }

    @Override
    public void newLine() {
        if (fileWriter == null) {
            setFilePath(DEFAULT_FILE_PATH);
        }
        try {
            fileWriter.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addComment(String comment) {
        if (fileWriter == null) {
            setFilePath(DEFAULT_FILE_PATH);
        }
        try {
            fileWriter.write("; " + comment + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addTag(String tag) {
        if (fileWriter == null) {
            setFilePath(DEFAULT_FILE_PATH);
        }
        try {
            fileWriter.write("\n" + tag + ":\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        if (fileWriter != null) {
            try {
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void changeToCodeSection() {
        try {
            fileWriter.write("\n.CODE\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void changeToDataSection() {
        try {
            fileWriter.write("\n.DATA\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void changeToHeapSection() {
        try {
            fileWriter.write("\n.HEAP\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void changeToStackSection() {
        try {
            fileWriter.write("\n.STACK\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
