package semantic_analyzer;

import java.util.Objects;

public class Visibility extends IVisibility {

    public Visibility(String name, String line, int row, int column) {
        super(name, line, row, column);
    }

    @Override
    public void consolidate() throws SemanticException {

    }

    @Override
    public void compareTo(Object o) throws SemanticException {
        if (o == null || getClass() != o.getClass()) throw new SemanticException(this, "diferentes");
        Visibility visibility = (Visibility) o;
        if (!Objects.equals(this.getName(), visibility.getName())) {
            throw new SemanticException(this, "nombres diferentes");
        }
    }

}
