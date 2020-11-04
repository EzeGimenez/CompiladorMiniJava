package semantic_analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolTable implements ISymbolTable {

    private static SymbolTable instance;
    private final Map<String, IClass> classMap;
    private final Map<String, IInterface> interfaceMap;
    private final List<SemanticException> semanticExceptionList;
    private IClass currClass;
    private IInterface currInterface;
    private IMethod currMethod;

    private SymbolTable() {
        interfaceMap = new HashMap<>();
        classMap = new HashMap<>();
        semanticExceptionList = new ArrayList<>();

        createDefaultClasses();
    }

    public static SymbolTable getInstance() {
        if (instance == null) {
            instance = new SymbolTable();
        }
        return instance;
    }

    public static void invalidate() { //TODO design concern
        instance = null;
    }

    private void createDefaultClasses() { // TODO design concern regarding their creation first
        IClass objectClass = new Class("Object");
        IClass systemClass = new Class("System");

        IAccessMode staticAccessMode = new AccessMode("static");
        IType intType = new IntType();
        IType booleanType = new BooleanType();
        IType charType = new CharType();
        IType stringType = new StringType();

        IMethod readMethod = new Method(staticAccessMode, intType, "read");
        IMethod printBMethod = new Method(staticAccessMode, null, "printB");
        IMethod printCMethod = new Method(staticAccessMode, null, "printC");
        IMethod printIMethod = new Method(staticAccessMode, null, "printI");
        IMethod printSMethod = new Method(staticAccessMode, null, "printS");
        IMethod printlnMethod = new Method(staticAccessMode, null, "println");
        IMethod printBlnMethod = new Method(staticAccessMode, null, "printBln");
        IMethod printClnMethod = new Method(staticAccessMode, null, "printCln");
        IMethod printIlnMethod = new Method(staticAccessMode, null, "printIln");
        IMethod printSlnMethod = new Method(staticAccessMode, null, "printSln");

        IParameter printBParameter = new Parameter("b", booleanType);
        IParameter printCParameter = new Parameter("c", charType);
        IParameter printIParameter = new Parameter("i", intType);
        IParameter printSParameter = new Parameter("s", stringType);
        IParameter printBlnParameter = new Parameter("b", booleanType);
        IParameter printClnParameter = new Parameter("c", charType);
        IParameter printIlnParameter = new Parameter("i", intType);
        IParameter printSlnParameter = new Parameter("s", stringType);

        printBMethod.addParameter(printBParameter);
        printCMethod.addParameter(printCParameter);
        printIMethod.addParameter(printIParameter);
        printSMethod.addParameter(printSParameter);
        printBlnMethod.addParameter(printBlnParameter);
        printClnMethod.addParameter(printClnParameter);
        printIlnMethod.addParameter(printIlnParameter);
        printSlnMethod.addParameter(printSlnParameter);

        systemClass.addMethod(readMethod);
        systemClass.addMethod(printBMethod);
        systemClass.addMethod(printCMethod);
        systemClass.addMethod(printIMethod);
        systemClass.addMethod(printSMethod);
        systemClass.addMethod(printlnMethod);
        systemClass.addMethod(printBlnMethod);
        systemClass.addMethod(printClnMethod);
        systemClass.addMethod(printIlnMethod);
        systemClass.addMethod(printSlnMethod);

        ClassType objectClassReference = new ClassType(objectClass.getName());
        systemClass.setParentClass(objectClassReference);

        addClass(objectClass);
        addClass(systemClass);
    }

    @Override
    public boolean containsInterface(String name) {
        return interfaceMap.containsKey(name);
    }

    @Override
    public boolean containsClass(String name) {
        return classMap.containsKey(name);
    }

    @Override
    public IClass getClass(String name) {
        return classMap.get(name);
    }

    @Override
    public IInterface getInterface(String name) {
        return interfaceMap.get(name);
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

    @Override
    public void consolidate() {
        for (IInterface i : interfaceMap.values()) {
            try {
                i.consolidate();
            } catch (SemanticException e) {
                saveException(e);
            }
        }
        for (IClass c : classMap.values()) {
            try {
                c.consolidate();
            } catch (SemanticException e) {
                saveException(e);
            }
        }
    }

    @Override
    public void saveException(SemanticException e) {
        semanticExceptionList.add(e);
    }

    @Override
    public void validate() throws SemanticException {
        if (semanticExceptionList.size() > 0) {
            SemanticException semanticException = semanticExceptionList.get(0);
            semanticExceptionList.remove(semanticException);
            throw semanticException;
        }
    }
}
