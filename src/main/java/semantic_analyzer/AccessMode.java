package semantic_analyzer;

import exceptions.SemanticException;

import java.util.Objects;

public class AccessMode extends IAccessMode {

    public AccessMode(String name) {
        this(name, "", 0, 0);
    }

    public AccessMode(String name, String line, int row, int column) {
        super(name, line, row, column);
    }

    @Override
    public void compareTo(Object obj) throws SemanticException {
        if (obj == null || this.getClass() != obj.getClass())
            throw new SemanticException(this, "");
        if (!Objects.equals(this.getName(), ((AccessMode) obj).getName())) {
            throw new SemanticException(this, "modo de acceso diferente");
        }
    }
}
