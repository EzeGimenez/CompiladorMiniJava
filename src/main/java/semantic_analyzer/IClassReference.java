package semantic_analyzer;

// TODO design concern
public abstract class IClassReference extends Entity {

    public IClassReference(String name, String line, int row, int column) {
        super(name, line, row, column);
    }

    public abstract IClassReference getGenericClass();

    public abstract void setGenericClass(IClassReference genericClass);

    /**
     * TODO design concern
     * Retorna la IClassReference si no coincide con la clase genericClass
     * (la IClassReference mas profunda si hay clases anidadas)
     * null si se encuentra
     *
     * @param genericClass IClassReference conteniendo la clase buscada
     * @return la IClassReference mas profunda si no se encuentra genericClass, null si se encuentra genericClass
     */
    public abstract IClassReference getDeepestMismatchClassRef(IClassReference genericClass);
}
