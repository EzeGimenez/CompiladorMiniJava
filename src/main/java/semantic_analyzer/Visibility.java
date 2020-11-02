package semantic_analyzer;

public class Visibility implements IVisibility {

    private final String visibility;

    public Visibility(String visibility) {
        this.visibility = visibility;
    }

    @Override
    public String getVisibility() {
        return visibility;
    }
}
