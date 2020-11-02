package semantic_analyzer;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable implements ISymbolTable {

    private static SymbolTable instance;
    private final Map<String, IClass> classMap;
    private final Map<String, IInterface> interfaceMap;
    private IClass currClass;
    private IInterface currInterface;
    private IMethod currMethod;

    private SymbolTable() {
        interfaceMap = new HashMap<>();
        classMap = new HashMap<>();
    }

    public static SymbolTable getInstance() {
        if (instance == null) {
            instance = new SymbolTable();
        }
        return instance;
    }

    @Override
    public void invalidate() { //TODO !!!!!!!!!!
        instance = null;
    }

    @Override
    public Map<String, IClass> getClassMap() {
        return classMap;
    }

    @Override
    public Map<String, IInterface> getInterfaceMap() {
        return interfaceMap;
    }

    @Override
    public IClass getCurrClass() {
        return currClass;
    }

    @Override
    public void setCurrClass(IClass currClass) {
        this.currClass = currClass;
    }

    @Override
    public IInterface getCurrInterface() {
        return currInterface;
    }

    @Override
    public void setCurrInterface(IInterface currInterface) {
        this.currInterface = currInterface;
    }

    @Override
    public void addInterface(IInterface iInterface) {
        interfaceMap.put(iInterface.getName(), iInterface);
    }

    @Override
    public void addClass(IClass iclass) {
        classMap.put(iclass.getName(), iclass);
    }

    @Override
    public IMethod getCurrMethod() {
        return currMethod;
    }

    @Override
    public void setCurrMethod(IMethod currMethod) {
        this.currMethod = currMethod;
    }
}
