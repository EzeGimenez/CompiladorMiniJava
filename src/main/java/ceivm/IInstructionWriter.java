package ceivm;

public interface IInstructionWriter {

    void setFilePath(String filePath);

    void write(String instruction, String argument, String comment);

    void write(String instruction, int argument, String comment);

    void write(String instruction, String argument);

    void write(String instruction, int argument);

    void write(String instruction);

    void newLine();

    void addComment(String comment);

    void addTag(String tag);

    void close();

    void changeToCodeSection();

    void changeToDataSection();

    void changeToHeapSection();

    void changeToStackSection();

}
