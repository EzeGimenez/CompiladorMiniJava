package semantic_analyzer;

public abstract class IAccessMode extends Entity {
    public IAccessMode(String name, String line, int row, int column) {
        super(name, line, row, column);
    }

    public abstract IAccessMode cloneForOverwrite(int row, int column);
}
