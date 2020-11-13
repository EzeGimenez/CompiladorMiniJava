package semantic_analyzer;

import exceptions.SemanticException;

import java.util.Map;

public interface ISymbolTable {
    Map<String, IClass> getClassMap();

    Map<String, IInterface> getInterfaceMap();

    IClass getCurrClass();

    void setCurrClass(IClass currClass);

    IInterface getCurrInterface();

    void setCurrInterface(IInterface currInterface);

    void addInterface(IInterface iInterface);

    void addClass(IClass iclass);

    IMethod getCurrMethod();

    void setCurrMethod(IMethod currMethod);

    boolean containsInterface(String name);

    boolean containsClass(String name);

    IClass getClass(String name);

    IInterface getInterface(String name);

    void consolidate();

    void saveException(SemanticException e);

    void validate() throws SemanticException;
}
