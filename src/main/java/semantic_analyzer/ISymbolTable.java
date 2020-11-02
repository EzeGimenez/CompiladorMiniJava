package semantic_analyzer;

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

    void invalidate();
}
