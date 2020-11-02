package semantic_analyzer;

public class AccessMode implements IAccessMode {
    private final String access;

    public AccessMode(String access) {
        this.access = access;
    }

    @Override
    public String getAccess() {
        return access;
    }
}
