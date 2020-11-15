package syntax_analyzer;

import exceptions.CompilerException;
import exceptions.LexicalException;
import exceptions.SemanticException;
import exceptions.SyntaxException;
import lexical_analyzer.*;
import semantic_analyzer.Class;
import semantic_analyzer.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static lexical_analyzer.TokenDescriptor.*;

public class SyntaxAnalyzer implements ISyntaxAnalyzer {

    private final ILexicalAnalyzer lexicalAnalyzer;
    private final FileHandler fileHandler;
    private final List<CompilerException> exceptionList;
    private final SymbolTable ST;
    private IToken currToken;

    public SyntaxAnalyzer(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
        lexicalAnalyzer = new LexicalAnalyzer(fileHandler);
        exceptionList = new ArrayList<>();

        ST = SymbolTable.getInstance();
    }

    @Override
    public void start() {
        inicial();
    }

    @Override
    public void validate() throws CompilerException {
        if (exceptionList.size() > 0) {
            CompilerException e = exceptionList.get(0);
            exceptionList.remove(e);
            throw e;
        }
    }

    private void updateToken() {
        try {
            currToken = lexicalAnalyzer.nextToken();
        } catch (LexicalException exception) {
            saveException(exception);
            updateToken();
        }
    }

    private void match(TokenDescriptor expectedDescriptor) throws SyntaxException {
        if (expectedDescriptor != currToken.getDescriptor()) {
            throw buildSyntaxException(expectedDescriptor.toString());
        } else {
            updateToken();
        }
    }

    private void updateTokenUntilSentinel(TokenDescriptor... sentinels) {
        boolean halt = false;
        while (!halt) {
            for (TokenDescriptor d : sentinels) {
                if (d == currToken.getDescriptor()) {
                    halt = true;
                    break;
                }
            }
            if (currToken.getDescriptor() == EOF) {
                halt = true;
            }
            if (!halt) {
                updateToken();
            }
        }
    }

    private SyntaxException buildSyntaxException(String expected) {
        String message = currToken.getLexeme();

        return new SyntaxException(
                fileHandler.getCurrentLine(),
                expected,
                message,
                fileHandler.getRow(),
                getColumn(message));
    }

    private SemanticException buildSemanticException(Entity entity, String message) {
        return new SemanticException(entity, message);
    }

    private int getColumn(String message) {
        switch (currToken.getDescriptor()) {
            case EOF:
                return fileHandler.getColumn() + message.length();
        }
        return fileHandler.getColumn();
    }

    private void saveException(CompilerException e) {
        exceptionList.add(e);
    }

    private boolean equalsAny(TokenDescriptor... descriptors) {
        TokenDescriptor currDescriptor = currToken.getDescriptor();
        for (TokenDescriptor d : descriptors) {
            if (d == currDescriptor) {
                return true;
            }
        }
        return false;
    }

    private void inicial() {
        try {
            updateToken();
            listaClases();
            match(EOF);

        } catch (CompilerException e) {
            saveException(e);
        }
    }

    private void listaClases() throws LexicalException, SyntaxException {
        try {
            if (equalsAny(CLASS)) {
                clase();
            } else if (equalsAny(INTERFACE)) {
                interfaz();
            } else {
                throw buildSyntaxException("clase o interfaz");
            }
        } catch (CompilerException e) {
            saveException(e);
            updateTokenUntilSentinel(CLASS, INTERFACE, EOF);
        }
        listaClasesAux();
    }

    private void listaClasesAux() throws LexicalException, SyntaxException {
        if (equalsAny(CLASS, INTERFACE)) {
            listaClases();
        } else if (!equalsAny(EOF)) {
            saveException(buildSyntaxException("una clase, interfaz o fin de archivo"));
            updateTokenUntilSentinel(CLASS, INTERFACE, EOF);
            listaClases();
        }
    }

    private void clase() throws SyntaxException, SemanticException {
        try {
            match(CLASS);
            String className = currToken.getLexeme();
            IClass classEntry = new Class(className, fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            match(ID_CLASS);

            ST.setCurrClass(classEntry);
            IClassType genericClass = genericidad();
            IClassType superClass = herencia();
            Collection<IClassType> interfaceList = implementa();

            ST.getCurrClass().setGenericType(genericClass);
            ST.getCurrClass().setParentClassRef(superClass);
            for (IClassType i : interfaceList) {
                if (!ST.getCurrClass().containsInterfaceInheritance(i.getName())) {
                    ST.getCurrClass().addInterfaceInheritance(i);
                } else {
                    saveException(buildSemanticException(i, "herencia de interfaz duplicada"));
                }
            }
        } catch (SyntaxException e) {
            saveException(e);
            updateTokenUntilSentinel(BRACES_OPEN);
        }
        match(BRACES_OPEN);
        listaMiembros();
        match(BRACES_CLOSE);

        if (!ST.containsClass(ST.getCurrClass().getName()) && !ST.containsInterface(ST.getCurrClass().getName())) {
            ST.addClass(ST.getCurrClass());
        } else {
            throw buildSemanticException(ST.getCurrClass(), "Ya fue declarada una clase/interfaz con el mismo nombre");
        }
    }

    private void interfaz() throws SyntaxException, SemanticException {
        try {
            match(INTERFACE);
            String interfaceName = currToken.getLexeme();
            IInterface interfaceEntry = new Interface(interfaceName, fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            match(ID_CLASS);

            ST.setCurrInterface(interfaceEntry);

            IClassType genericClass = genericidad();
            Collection<IClassType> interfaceList = herenciaInterfaz();

            ST.getCurrInterface().setGenericType(genericClass);
            for (IClassType i : interfaceList) {
                ST.getCurrInterface().addInheritance(i);
            }

        } catch (SyntaxException e) {
            saveException(e);
            updateTokenUntilSentinel(BRACES_OPEN);
        }
        match(BRACES_OPEN);
        listaMiembrosInterfaz();
        match(BRACES_CLOSE);

        if (!ST.containsClass(ST.getCurrInterface().getName()) && !ST.containsInterface(ST.getCurrInterface().getName())) {
            ST.addInterface(ST.getCurrInterface());
        } else {
            throw buildSemanticException(ST.getCurrInterface(), "Ya fue declarada una clase/interfaz con el mismo nombre");
        }
    }

    private IClassType genericidad() throws SyntaxException {
        IClassType outClassRef = null;
        if (equalsAny(LESS_THAN)) {
            match(LESS_THAN);
            String className = currToken.getLexeme();
            outClassRef = new ClassType(className, fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            match(ID_CLASS);
            IClassType genericClass = genericidad();
            match(GREATER_THAN);
            outClassRef.setGenericType(genericClass);

        } else if (!equalsAny(EXTENDS, IMPLEMENTS, BRACES_OPEN, GREATER_THAN, COMMA, ID_MET_VAR, DOT, ASSIGN, ASSIGN_ADD, ASSIGN_SUB, SEMICOLON, PARENTHESES_OPEN)) {
            throw buildSyntaxException("token siguiente a genericidad");
        }
        return outClassRef;
    }

    private IClassType genericidadImplicita() throws SyntaxException {
        IClassType outClassRef = null;
        if (equalsAny(LESS_THAN)) {
            match(LESS_THAN);
            outClassRef = genericidadImplicitaAUX();
            match(GREATER_THAN);
        } else if (!equalsAny(EXTENDS, IMPLEMENTS, BRACES_OPEN, GREATER_THAN, COMMA, ID_MET_VAR, DOT, ASSIGN, ASSIGN_ADD, ASSIGN_SUB, SEMICOLON, PARENTHESES_OPEN)) {
            throw buildSyntaxException("token siguiente a genericidad");
        }
        return outClassRef;
    }

    private IClassType genericidadImplicitaAUX() throws SyntaxException {
        IClassType outClassRef = null;
        if (equalsAny(ID_CLASS)) {
            String className = currToken.getLexeme();
            outClassRef = new ClassType(className, fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            match(ID_CLASS);
            IClassType genericClass = genericidad();
            outClassRef.setGenericType(genericClass);
        } else if (!equalsAny(GREATER_THAN)) {
            throw buildSyntaxException(">");
        }
        return outClassRef;
    }

    private IClassType herencia() throws SyntaxException {
        if (equalsAny(EXTENDS)) {
            match(EXTENDS);
            String className = currToken.getLexeme();
            IClassType classReference = new ClassType(className, fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            match(ID_CLASS);
            IClassType genericClass = genericidad();
            classReference.setGenericType(genericClass);

            return classReference;
        } else if (!equalsAny(IMPLEMENTS, BRACES_OPEN)) {
            throw buildSyntaxException("extends, implements o {");
        }
        return new ClassType("Object", fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
    }

    private Collection<IClassType> implementa() throws SyntaxException {
        Collection<IClassType> inheritanceEntities = new ArrayList<>();
        if (equalsAny(IMPLEMENTS)) {
            match(IMPLEMENTS);
            inheritanceEntities = listaInterfaces();
        } else if (!equalsAny(BRACES_OPEN)) {
            throw buildSyntaxException("implements o {");
        }
        return inheritanceEntities;
    }

    private Collection<IClassType> listaInterfaces() throws SyntaxException {
        String className = currToken.getLexeme();
        IClassType inheritanceEntity = new ClassType(className, fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
        match(ID_CLASS);
        IClassType genericClass = genericidad();
        inheritanceEntity.setGenericType(genericClass);

        Collection<IClassType> out = listaInterfacesAux();
        out.add(inheritanceEntity);
        return out;
    }

    private Collection<IClassType> listaInterfacesAux() throws SyntaxException {
        if (equalsAny(COMMA)) {
            match(COMMA);
            return listaInterfaces();
        } else if (!equalsAny(BRACES_OPEN)) {
            throw buildSyntaxException("coma o {");
        }
        return new ArrayList<>();
    }

    private Collection<IClassType> herenciaInterfaz() throws SyntaxException {
        if (equalsAny(EXTENDS)) {
            match(EXTENDS);
            return listaInterfaces();
        } else if (!equalsAny(BRACES_OPEN)) {
            throw buildSyntaxException("extends o {");
        }
        return new ArrayList<>();
    }

    private void listaMiembros() throws SyntaxException {
        if (equalsAny(PUBLIC, PROTECTED, PRIVATE, ID_CLASS, STATIC, DYNAMIC, PR_BOOLEAN, PR_CHAR, PR_INT, PR_STRING)) {
            try {
                miembro();
            } catch (CompilerException e) {
                saveException(e);
                updateTokenUntilSentinel(ID_CLASS, PUBLIC, PROTECTED, PRIVATE, STATIC, DYNAMIC, BRACES_CLOSE);
            }
            listaMiembros();
        } else if (!equalsAny(BRACES_CLOSE)) {
            throw buildSyntaxException("constructor, metodo, atributo o }");
        }
    }

    private void listaMiembrosInterfaz() throws SyntaxException {
        if (equalsAny(STATIC, DYNAMIC)) {
            try {
                metodoDeclaracion();
            } catch (CompilerException e) {
                saveException(e);
                updateTokenUntilSentinel(SEMICOLON, ID_CLASS, PUBLIC, PROTECTED, PRIVATE, STATIC, DYNAMIC, BRACES_CLOSE);
            }
            listaMiembrosInterfaz();
        } else if (!equalsAny(BRACES_CLOSE)) {
            throw buildSyntaxException("metodo o }");
        }
    }

    private void miembro() throws LexicalException, SyntaxException, SemanticException {
        if (equalsAny(PUBLIC, PROTECTED, PRIVATE, ID_CLASS, PR_STRING, PR_BOOLEAN, PR_CHAR, PR_INT)) {
            attrVisibilidad();
        } else if (equalsAny(STATIC, DYNAMIC)) {
            metodoConCuerpo();
        } else {
            throw buildSyntaxException("metodo, constructor o atributo");
        }
    }

    private void attrVisibilidad() throws SyntaxException, LexicalException, SemanticException {
        if (equalsAny(PUBLIC)) {
            IVisibility visibility = new Visibility(currToken.getLexeme(), fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            match(PUBLIC);
            attr(visibility);
        } else if (equalsAny(PROTECTED)) {
            IVisibility visibility = new Visibility(currToken.getLexeme(), fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            match(PROTECTED);
            attr(visibility);
        } else if (equalsAny(PRIVATE)) {
            IVisibility visibility = new Visibility(currToken.getLexeme(), fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            match(PRIVATE);
            attr(visibility);
        } else if (equalsAny(ID_CLASS, PR_BOOLEAN, PR_CHAR, PR_INT, PR_STRING)) {
            attrOCons();
        }
    }

    private void attr(IVisibility visibility) throws SyntaxException, LexicalException, SemanticException {
        IAccessMode accessMode = estaticoOVacio();
        IType type = tipo();
        asignacionAttr(visibility, accessMode, type);
        match(SEMICOLON);
    }

    private IAccessMode estaticoOVacio() throws SyntaxException {
        if (equalsAny(STATIC)) {
            String accessType = currToken.getLexeme();
            match(STATIC);
            return new AccessMode(accessType, fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
        } else if (!equalsAny(ID_CLASS, PR_BOOLEAN, PR_CHAR, PR_INT, PR_STRING)) {
            throw buildSyntaxException("static o tipo");
        }
        return null;
    }

    private void attrOCons() throws SyntaxException, LexicalException, SemanticException {
        if (equalsAny(ID_CLASS)) {
            String className = currToken.getLexeme();
            IClassType classReference = new ClassType(className, fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            match(ID_CLASS);
            attrOConsAUX(classReference);
        } else if (equalsAny(PR_BOOLEAN, PR_CHAR, PR_INT, PR_STRING)) {
            IVisibility defaultVisibility = new Visibility("public", fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            IAccessMode defaultAccessMode = new AccessMode("static", fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());

            IType type = tipoPrimitivo();
            asignacionAttr(defaultVisibility, defaultAccessMode, type);
            match(SEMICOLON);
        } else {
            throw buildSyntaxException("tipo");
        }
    }

    private void attrOConsAUX(IClassType classType) throws SyntaxException, SemanticException, LexicalException {
        if (equalsAny(LESS_THAN)) {
            IClassType genericClass = genericidad();
            classType.setGenericType(genericClass);

            IVisibility defaultVisibility = new Visibility("public", fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            IAccessMode defaultAccessMode = new AccessMode("static", fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());

            asignacionAttr(defaultVisibility, defaultAccessMode, classType);
            match(SEMICOLON);
        } else if (equalsAny(PARENTHESES_OPEN, ID_MET_VAR)) {
            constructorOAttr(classType);
        } else {
            throw buildSyntaxException("genericidad o attr");
        }
    }

    private void constructorOAttr(IClassType classType) throws SyntaxException, LexicalException, SemanticException {
        if (equalsAny(PARENTHESES_OPEN)) {
            constructor(classType);
        } else if (equalsAny(ID_MET_VAR)) {
            IVisibility defaultVisibility = new Visibility("public", fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            IAccessMode defaultAccessMode = new AccessMode("static", fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());

            asignacionAttr(defaultVisibility, defaultAccessMode, classType);
            match(SEMICOLON);
        } else {
            throw buildSyntaxException("( o id var o metodo");
        }
    }

    private void constructor(IClassType classType) throws SyntaxException, LexicalException, SemanticException {
        IMethod constructor = new Constructor(classType.getName(), classType, fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
        ST.setCurrMethod(constructor);
        argsFormales();
        bloque();
        if (ST.getCurrClass().getConstructor() == null) {
            if (ST.getCurrClass().getName().equals(constructor.getName())) {
                ST.getCurrClass().setConstructor(constructor);
            } else {
                throw buildSemanticException(ST.getCurrMethod(), "metodo mal definido o constructor con nombre distinto a la clase");
            }
        } else {
            throw buildSemanticException(ST.getCurrMethod(), "dos constructores en una misma clase");
        }
    }

    private void asignacionAttr(IVisibility visibility, IAccessMode accessMode, IType type) throws SyntaxException, LexicalException, SemanticException {
        String attributeName = currToken.getLexeme();
        IVariable attribute = new Variable(attributeName, visibility, type, fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
        match(ID_MET_VAR);
        asignacionAttrAux(visibility, accessMode, type);

        if (!ST.getCurrClass().containsAttribute(attributeName)) {
            ST.getCurrClass().addAttribute(attribute);
        } else {
            throw buildSemanticException(attribute, "nombre de atributo duplicado");
        }
    }

    private void asignacionAttrAux(IVisibility visibility, IAccessMode accessMode, IType type) throws SyntaxException, LexicalException, SemanticException {
        if (equalsAny(ASSIGN)) {
            match(ASSIGN);
            expresion();
        }
        listaAsignacion(visibility, accessMode, type);
    }

    private void listaAsignacion(IVisibility visibility, IAccessMode accessMode, IType type) throws SyntaxException, LexicalException, SemanticException {
        if (equalsAny(COMMA)) {
            match(COMMA);
            asignacionAttr(visibility, accessMode, type);
        } else if (!equalsAny(SEMICOLON)) {
            throw buildSyntaxException("nombre o asignacion de variables");
        }
    }

    private void cabeceraMetodo() throws LexicalException, SyntaxException {
        IAccessMode accessMode = formaMetodo();
        IType type = tipoMetodo();
        String methodName = currToken.getLexeme();
        IMethod newMethod = new Method(accessMode, type, methodName, fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
        match(ID_MET_VAR);
        ST.setCurrMethod(newMethod);

        argsFormales();
    }

    private void metodoConCuerpo() throws SyntaxException, LexicalException, SemanticException {
        try {
            cabeceraMetodo();
        } catch (SyntaxException e) {
            saveException(e);
            updateTokenUntilSentinel(BRACES_OPEN);
        }
        bloque();
        if (!ST.getCurrClass().containsMethod(ST.getCurrMethod().getName())) {
            ST.getCurrClass().addMethod(ST.getCurrMethod());
        } else {
            throw buildSemanticException(ST.getCurrMethod(), "nombre metodo duplicado");
        }
    }

    private void metodoDeclaracion() throws SyntaxException, LexicalException, SemanticException {
        cabeceraMetodo();
        if (!ST.getCurrInterface().containsMethod(ST.getCurrMethod().getName())) {
            ST.getCurrInterface().addMethod(ST.getCurrMethod());
        } else {
            throw buildSemanticException(ST.getCurrMethod(), "nombre metodo duplicado");
        }

        match(SEMICOLON);
    }

    private IType tipo() throws SyntaxException {
        IType outType;
        if (equalsAny(PR_BOOLEAN, PR_CHAR, PR_INT, PR_STRING)) {
            outType = tipoPrimitivo();
        } else if (equalsAny(ID_CLASS)) {
            String typeClassName = currToken.getLexeme();
            IClassType classType = new ClassType(typeClassName, fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            match(ID_CLASS);

            IClassType genericClass = genericidad();
            classType.setGenericType(genericClass);

            outType = classType;
        } else {
            throw buildSyntaxException("una definicion de tipo");
        }
        return outType;
    }

    private IType tipoPrimitivo() throws SyntaxException {
        PrimitiveType primitiveType;
        if (equalsAny(PR_BOOLEAN)) {
            primitiveType = new BooleanType(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            match(PR_BOOLEAN);

        } else if (equalsAny(PR_CHAR)) {
            primitiveType = new CharType(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            match(PR_CHAR);

        } else if (equalsAny(PR_INT)) {
            primitiveType = new IntType(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            match(PR_INT);

        } else if (equalsAny(PR_STRING)) {
            primitiveType = new StringType(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            match(PR_STRING);

        } else {
            throw buildSyntaxException("boolean, char, int o String");
        }
        return primitiveType;
    }

    private IAccessMode formaMetodo() throws SyntaxException {
        String accesModeName = currToken.getLexeme();
        IAccessMode accessMode = new AccessMode(accesModeName, fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
        if (equalsAny(STATIC)) {
            match(STATIC);
        } else if (equalsAny(DYNAMIC)) {
            match(DYNAMIC);
        } else {
            throw buildSyntaxException("static o dynamic");
        }
        return accessMode;
    }

    private IType tipoMetodo() throws SyntaxException {
        IType outType;
        if (equalsAny(ID_CLASS, PR_BOOLEAN, PR_CHAR, PR_INT, PR_STRING)) {
            outType = tipo();
        } else if (equalsAny(VOID)) {
            outType = new VoidType(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            match(VOID);
        } else {
            throw buildSyntaxException("el tipo de retorno");
        }
        return outType;
    }

    private void argsFormales() throws LexicalException, SyntaxException {
        match(PARENTHESES_OPEN);
        listaArgsFormalesOVacio();
        match(PARENTHESES_CLOSE);
    }

    private void listaArgsFormalesOVacio() throws LexicalException, SyntaxException {
        if (equalsAny(ID_CLASS, PR_BOOLEAN, PR_CHAR, PR_INT, PR_STRING)) {
            listaArgsFormales();
        } else if (!equalsAny(PARENTHESES_CLOSE)) {
            throw buildSyntaxException("argumento o )");
        }
    }

    private void listaArgsFormales() throws LexicalException, SyntaxException {
        try {
            argFormal();
        } catch (CompilerException e) {
            saveException(e);
            updateTokenUntilSentinel(COMMA, PARENTHESES_CLOSE);
        }
        listaArgsFormalesAUX();
    }

    private void listaArgsFormalesAUX() throws LexicalException, SyntaxException {
        if (equalsAny(COMMA)) {
            match(COMMA);
            listaArgsFormales();
        } else if (!equalsAny(PARENTHESES_CLOSE)) {
            throw buildSyntaxException("otro argumento o )");
        }
    }

    private void argFormal() throws SyntaxException, SemanticException {
        IType type = tipo();
        String parameterName = currToken.getLexeme();
        IParameter parameter = new Parameter(parameterName, type, fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());

        match(ID_MET_VAR);

        if (!ST.getCurrMethod().containsParameter(parameterName)) {
            ST.getCurrMethod().addParameter(parameter);
        } else {
            throw buildSemanticException(parameter, "nombre de parametro duplicado");
        }
    }

    private void bloque() throws SyntaxException {
        match(BRACES_OPEN);
        try {
            listaSentencias();
        } catch (CompilerException e) {
            saveException(e);
            updateTokenUntilSentinel(BRACES_CLOSE);
        }
        match(BRACES_CLOSE);
    }

    private void listaSentencias() throws SyntaxException {
        if (equalsAny(SEMICOLON, STATIC, IF, WHILE, RETURN, ID_CLASS, BRACES_OPEN, PARENTHESES_OPEN, PR_BOOLEAN, PR_CHAR, PR_INT, PR_STRING, THIS, NEW, ID_MET_VAR)) {

            try {
                sentencia();
            } catch (CompilerException e) {
                saveException(e);
                updateTokenUntilSentinel(SEMICOLON, BRACES_OPEN, BRACES_CLOSE);
            }

            listaSentencias();
        } else if (!equalsAny(BRACES_CLOSE)) {
            throw buildSyntaxException("una sentencia o }");
        }
    }

    private void sentencia() throws LexicalException, SyntaxException, SemanticException {
        if (equalsAny(SEMICOLON)) {
            match(SEMICOLON);
        } else if (equalsAny(PARENTHESES_OPEN, THIS, NEW, ID_MET_VAR)) {
            acceso();
            sentenciaAUX();
            match(SEMICOLON);
        } else if (equalsAny(STATIC)) {
            match(STATIC);
            match(ID_CLASS);
            genericidad();
            preAccesoEstatico();
            match(SEMICOLON);
        } else if (equalsAny(PR_BOOLEAN, PR_CHAR, PR_INT, PR_STRING)) {
            IAccessMode defaultAccessMode = new AccessMode("static", fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            IType type = tipoPrimitivo();
            asignacionAttr(null, defaultAccessMode, type);
            match(SEMICOLON);
        } else if (equalsAny(ID_CLASS)) {
            String classTypeName = currToken.getLexeme();
            IClassType classType = new ClassType(classTypeName, fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            match(ID_CLASS);

            IClassType genericClass = genericidad();
            classType.setGenericType(genericClass);

            accesoEstaticoODeclaracion(classType);
            match(SEMICOLON);
        } else if (equalsAny(IF)) {
            match(IF);
            match(PARENTHESES_OPEN);
            expresion();
            match(PARENTHESES_CLOSE);
            sentencia();
            sentenciaAUX1();
        } else if (equalsAny(WHILE)) {
            match(WHILE);
            match(PARENTHESES_OPEN);
            expresion();
            match(PARENTHESES_CLOSE);
            sentencia();
        } else if (equalsAny(BRACES_OPEN)) {
            bloque();
        } else if (equalsAny(RETURN)) {
            match(RETURN);
            expresionOVacio();
            match(SEMICOLON);
        } else {
            throw buildSyntaxException("comienzo de una sentencia");
        }
    }

    private void sentenciaAUX() throws LexicalException, SyntaxException {
        if (equalsAny(ASSIGN, ASSIGN_ADD, ASSIGN_SUB)) {
            asignacion();
        } else if (!equalsAny(SEMICOLON)) {
            throw buildSyntaxException(";");
        }
    }

    private void sentenciaAUX1() throws LexicalException, SyntaxException, SemanticException {
        if (equalsAny(ELSE)) {
            match(ELSE);
            sentencia();
        } else if (!equalsAny(SEMICOLON, IF, WHILE, RETURN, BRACES_OPEN, PARENTHESES_OPEN, PR_BOOLEAN, PR_CHAR, PR_INT, PR_STRING, THIS, NEW, ID_MET_VAR, ELSE, BRACES_CLOSE)) {
            throw buildSyntaxException("; if while return idclase { ( boolean char int string this static new idmetvar else }");
        }
    }

    private void accesoEstaticoODeclaracion(IType classType) throws SyntaxException, LexicalException, SemanticException {
        if (equalsAny(ID_MET_VAR)) {
            IAccessMode defaultAccessMode = new AccessMode("static", fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            asignacionAttr(null, defaultAccessMode, classType);
        } else if (equalsAny(DOT)) {
            preAccesoEstatico();
        } else {
            throw buildSyntaxException("idMetVar o .");
        }
    }

    private void asignacion() throws LexicalException, SyntaxException {
        tipoDeAsignacion();
        expresion();
    }

    private void tipoDeAsignacion() throws SyntaxException {
        if (equalsAny(ASSIGN)) {
            match(ASSIGN);
        } else if (equalsAny(ASSIGN_SUB)) {
            match(ASSIGN_SUB);
        } else if (equalsAny(ASSIGN_ADD)) {
            match(ASSIGN_ADD);
        } else {
            throw buildSyntaxException("= += -=");
        }
    }

    private void expresionOVacio() throws LexicalException, SyntaxException {
        if (equalsAny(ADD, SUB, OP_NOT, NULL, ID_CLASS, BOOLEAN, INTEGER, CHARACTER, STRING, PARENTHESES_OPEN, THIS, NEW, ID_MET_VAR)) {
            expresion();
        } else if (!equalsAny(SEMICOLON)) {
            throw buildSyntaxException("valor o ;");
        }
    }

    private void expresion() throws LexicalException, SyntaxException {
        or();
    }

    private void or() throws SyntaxException, LexicalException {
        and();
        orAux();
    }

    private void orAux() throws SyntaxException, LexicalException {
        if (equalsAny(OP_OR)) {
            op1();
            and();
            orAux();
        } else if (!equalsAny(PARENTHESES_CLOSE, COMMA, SEMICOLON)) {
            throw buildSyntaxException("|| ) , ;");
        }
    }

    private void and() throws SyntaxException, LexicalException {
        equalsExp();
        andAux();
    }

    private void andAux() throws SyntaxException, LexicalException {
        if (equalsAny(OP_AND)) {
            op2();
            equalsExp();
            andAux();
        } else if (!equalsAny(OP_OR, PARENTHESES_CLOSE, COMMA, SEMICOLON)) {
            throw buildSyntaxException("&& || ) , ;");
        }
    }

    private void equalsExp() throws SyntaxException, LexicalException {
        inEq();
        equalsAux();
    }

    private void equalsAux() throws SyntaxException, LexicalException {
        if (equalsAny(EQUALS, NOT_EQUALS)) {
            op3();
            inEq();
            equalsAux();
        } else if (!equalsAny(OP_AND, OP_OR, PARENTHESES_CLOSE, COMMA, SEMICOLON)) {
            throw buildSyntaxException("== != && || ) , ;");
        }
    }

    private void inEq() throws SyntaxException, LexicalException {
        add();
        inEqAux();
    }

    private void inEqAux() throws SyntaxException, LexicalException {
        if (equalsAny(LESS_THAN, GREATER_THAN, LESS_EQUALS, GREATER_EQUALS)) {
            op4();
            add();
            inEqAux();
        } else if (!equalsAny(EQUALS, NOT_EQUALS, OP_AND, OP_OR, PARENTHESES_CLOSE, COMMA, SEMICOLON)) {
            throw buildSyntaxException("operador binario");
        }
    }

    private void add() throws SyntaxException, LexicalException {
        mult();
        addAux();
    }

    private void addAux() throws SyntaxException, LexicalException {
        if (equalsAny(ADD, SUB)) {
            op5();
            mult();
            addAux();
        } else if (!equalsAny(LESS_THAN, GREATER_THAN, LESS_EQUALS, GREATER_EQUALS, EQUALS, NOT_EQUALS, OP_AND, OP_OR, PARENTHESES_CLOSE, COMMA, SEMICOLON)) {
            throw buildSyntaxException("|| ) , ;");
        }
    }

    private void mult() throws SyntaxException, LexicalException {
        expresionUnaria();
        multAux();
    }

    private void multAux() throws SyntaxException, LexicalException {
        if (equalsAny(MULTIPLY, DIVIDE, REMAINDER)) {
            op6();
            expresionUnaria();
            multAux();
        } else if (!equalsAny(ADD, SUB, LESS_THAN, GREATER_THAN, LESS_EQUALS, GREATER_EQUALS, EQUALS, NOT_EQUALS, OP_AND, OP_OR, PARENTHESES_CLOSE, COMMA, SEMICOLON)) {
            throw buildSyntaxException("multiplicacion, suma, in/ecuacion, operador booleano ) , ;");
        }
    }

    private void op1() throws SyntaxException {
        match(OP_OR);
    }

    private void op2() throws SyntaxException {
        match(OP_AND);
    }

    private void op3() throws SyntaxException {
        if (equalsAny(EQUALS)) {
            match(EQUALS);
        } else if (equalsAny(NOT_EQUALS)) {
            match(NOT_EQUALS);
        } else {
            throw buildSyntaxException("== o !=");
        }
    }

    private void op4() throws SyntaxException {
        if (equalsAny(LESS_THAN)) {
            match(LESS_THAN);
        } else if (equalsAny(GREATER_THAN)) {
            match(GREATER_THAN);
        } else if (equalsAny(LESS_EQUALS)) {
            match(LESS_EQUALS);
        } else if (equalsAny(GREATER_EQUALS)) {
            match(GREATER_EQUALS);
        } else {
            throw buildSyntaxException("< > <= >=");
        }
    }

    private void op5() throws SyntaxException {
        if (equalsAny(ADD)) {
            match(ADD);
        } else if (equalsAny(SUB)) {
            match(SUB);
        } else {
            throw buildSyntaxException("+ o -");
        }
    }

    private void op6() throws SyntaxException {
        if (equalsAny(MULTIPLY)) {
            match(MULTIPLY);
        } else if (equalsAny(DIVIDE)) {
            match(DIVIDE);
        } else if (equalsAny(REMAINDER)) {
            match(REMAINDER);
        } else {
            throw buildSyntaxException("*, / o %");
        }
    }

    private void expresionUnaria() throws LexicalException, SyntaxException {
        if (equalsAny(ADD, SUB, OP_NOT)) {
            operadorUnario();
            operando();
        } else if (equalsAny(NULL, BOOLEAN, INTEGER, CHARACTER, STRING, PARENTHESES_OPEN, ID_CLASS, THIS, NEW, ID_MET_VAR, STATIC)) {
            operando();
        } else {
            throw buildSyntaxException("una expresion u operando");
        }
    }

    private void operadorUnario() throws SyntaxException {
        if (equalsAny(ADD)) {
            match(ADD);
        } else if (equalsAny(SUB)) {
            match(SUB);
        } else if (equalsAny(OP_NOT)) {
            match(OP_NOT);
        } else {
            throw buildSyntaxException("+ - !");
        }
    }

    private void operando() throws LexicalException, SyntaxException {
        if (equalsAny(NULL, BOOLEAN, INTEGER, CHARACTER, STRING)) {
            literal();
        } else if (equalsAny(STATIC, ID_CLASS, PARENTHESES_OPEN, THIS, NEW, ID_MET_VAR)) {
            accesoOperando();
        } else {
            throw buildSyntaxException("literal o modo de acceso");
        }
    }

    private void literal() throws SyntaxException {
        if (equalsAny(NULL)) {
            match(NULL);
        } else if (equalsAny(BOOLEAN)) {
            match(BOOLEAN);
        } else if (equalsAny(INTEGER)) {
            match(INTEGER);
        } else if (equalsAny(CHARACTER)) {
            match(CHARACTER);
        } else if (equalsAny(STRING)) {
            match(STRING);
        } else {
            throw buildSyntaxException("tipo primitivo o null");
        }
    }

    private void preAccesoEstatico() throws SyntaxException, LexicalException {
        varOMetodoEncadenado();
        encadenado();
        sentenciaAUX();
    }

    private void acceso() throws LexicalException, SyntaxException {
        primario();
        encadenado();
    }

    private void accesoOperando() throws SyntaxException, LexicalException {
        if (equalsAny(PARENTHESES_OPEN, THIS, NEW, ID_MET_VAR)) {
            acceso();
        } else if (equalsAny(STATIC, ID_CLASS)) {
            accesoEstatico();
            encadenado();
        } else {
            throw buildSyntaxException("tipo de acceso");
        }
    }

    private void primario() throws LexicalException, SyntaxException {
        if (equalsAny(THIS)) {
            accesoThis();
        } else if (equalsAny(NEW)) {
            accesoConstructor();
        } else if (equalsAny(ID_MET_VAR)) {
            accesoVarOMetodo();
        } else if (equalsAny(PARENTHESES_OPEN)) {
            match(PARENTHESES_OPEN);
            expresion();
            match(PARENTHESES_CLOSE);
        } else {
            throw buildSyntaxException("acceso primario");
        }
    }

    private void accesoThis() throws SyntaxException {
        if (equalsAny(THIS)) {
            match(THIS);
        } else {
            throw buildSyntaxException("this");
        }
    }

    private void accesoVarOMetodo() throws SyntaxException, LexicalException {
        if (equalsAny(ID_MET_VAR)) {
            match(ID_MET_VAR);
            accesoVarOMetodoAUX();
        } else {
            throw buildSyntaxException("id met var");
        }
    }

    private void accesoVarOMetodoAUX() throws SyntaxException, LexicalException {
        if (equalsAny(PARENTHESES_OPEN)) {
            argsActuales();
        } else if (!equalsAny(SEMICOLON, DOT, EQUALS, NOT_EQUALS, LESS_THAN, GREATER_THAN, LESS_EQUALS, GREATER_EQUALS, ADD, SUB, MULTIPLY, DIVIDE, REMAINDER, OP_AND, OP_OR, PARENTHESES_CLOSE, COMMA, ASSIGN, ASSIGN_ADD, ASSIGN_SUB)) {
            throw buildSyntaxException("una expresion o )");
        }
    }

    private void accesoEstatico() throws SyntaxException, LexicalException {
        if (equalsAny(STATIC)) {
            match(STATIC);
            match(ID_CLASS);
            varOMetodoEncadenado();
            encadenado();
        } else if (equalsAny(ID_CLASS)) {
            match(ID_CLASS);
            varOMetodoEncadenado();
            encadenado();
        } else {
            throw buildSyntaxException("static o idClase");
        }
    }

    private void accesoConstructor() throws LexicalException, SyntaxException {
        if (equalsAny(NEW)) {
            match(NEW);
            match(ID_CLASS);
            IClassType genericidad = genericidadImplicita();
            argsActuales();
        } else {
            throw buildSyntaxException("new");
        }
    }

    private void argsActuales() throws LexicalException, SyntaxException {
        match(PARENTHESES_OPEN);
        listaExpsOVacio();
        match(PARENTHESES_CLOSE);
    }

    private void listaExpsOVacio() throws LexicalException, SyntaxException {
        if (equalsAny(ADD, SUB, OP_NOT, NULL, BOOLEAN, INTEGER, CHARACTER, STRING, PARENTHESES_OPEN, THIS, ID_CLASS, NEW, ID_MET_VAR)) {
            listaExps();
        } else if (!equalsAny(PARENTHESES_CLOSE)) {
            throw buildSyntaxException(")");
        }
    }

    private void listaExps() throws LexicalException, SyntaxException {
        try {
            expresion();
        } catch (CompilerException e) {
            saveException(e);
            updateTokenUntilSentinel(COMMA, PARENTHESES_CLOSE);
        }
        listaExpsAUX();
    }

    private void listaExpsAUX() throws LexicalException, SyntaxException {
        if (equalsAny(COMMA)) {
            match(COMMA);
            listaExps();
        } else if (!equalsAny(PARENTHESES_CLOSE)) {
            throw buildSyntaxException(") u otra expresion");
        }
    }

    private void encadenado() throws LexicalException, SyntaxException {
        if (equalsAny(DOT)) {
            varOMetodoEncadenado();
            encadenado();
        } else if (!equalsAny(MULTIPLY, DIVIDE, REMAINDER, ADD, SUB, LESS_THAN, GREATER_THAN, LESS_EQUALS, GREATER_EQUALS, EQUALS, NOT_EQUALS, OP_AND, OP_OR, ASSIGN, ASSIGN_ADD, ASSIGN_SUB, PARENTHESES_CLOSE, COMMA, SEMICOLON)) {
            throw buildSyntaxException(". asignacion operacion binaria ; ");
        }
    }

    private void varOMetodoEncadenado() throws LexicalException, SyntaxException {
        try {
            match(DOT);
            match(ID_MET_VAR);
        } catch (CompilerException e) {
            saveException(e);
            updateTokenUntilSentinel(PARENTHESES_OPEN, SEMICOLON);
        }
        varOMetodoEncadenadoAUX();
    }

    private void varOMetodoEncadenadoAUX() throws LexicalException, SyntaxException {
        if (equalsAny(PARENTHESES_OPEN)) {
            argsActuales();
        } else if (!equalsAny(DOT, MULTIPLY, DIVIDE, REMAINDER, ADD, SUB, LESS_THAN, GREATER_THAN, LESS_EQUALS, GREATER_EQUALS, EQUALS, NOT_EQUALS, OP_AND, OP_OR, ASSIGN, ASSIGN_ADD, ASSIGN_SUB, PARENTHESES_CLOSE, COMMA, SEMICOLON)) {
            throw buildSyntaxException("metodo encadenado, asignacion u operador");
        }
    }
}
