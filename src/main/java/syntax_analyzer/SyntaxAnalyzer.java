package syntax_analyzer;

import exceptions.CompilerException;
import exceptions.LexicalException;
import exceptions.SemanticException;
import exceptions.SyntaxException;
import lexical_analyzer.*;
import semantic_analyzer.Class;
import semantic_analyzer.*;
import semantic_analyzer_ast.expression_nodes.*;
import semantic_analyzer_ast.sentence_nodes.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
        if (currToken.getDescriptor() == EOF) {
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
            outClassRef = new TypeClass(className, fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
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
            outClassRef = new TypeClass(className, fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
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
            IClassType classReference = new TypeClass(className, fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            match(ID_CLASS);
            IClassType genericClass = genericidad();
            classReference.setGenericType(genericClass);

            return classReference;
        } else if (!equalsAny(IMPLEMENTS, BRACES_OPEN)) {
            throw buildSyntaxException("extends, implements o {");
        }
        return new TypeClass("Object", fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
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
        IClassType inheritanceEntity = new TypeClass(className, fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
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
            IClassType classReference = new TypeClass(className, fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            match(ID_CLASS);
            attrOConsAUX(classReference);
        } else if (equalsAny(PR_BOOLEAN, PR_CHAR, PR_INT, PR_STRING)) {
            IVisibility defaultVisibility = new Visibility("public", fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());

            IType type = tipoPrimitivo();
            asignacionAttr(defaultVisibility, null, type);
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

            asignacionAttr(defaultVisibility, null, classType);
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

            asignacionAttr(defaultVisibility, null, classType);
            match(SEMICOLON);
        } else {
            throw buildSyntaxException("( o id var o metodo");
        }
    }

    private void constructor(IClassType classType) throws SyntaxException, LexicalException, SemanticException {
        IMethod constructor = new Constructor(classType.getName(), classType, fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
        ST.setCurrMethod(constructor);
        argsFormales();
        constructor.setAbstractSyntaxTree(bloque());
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
        IVariable attribute = new Variable(attributeName, accessMode, visibility, type, fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());

        DeclarationNode declarationNode = new DeclarationNode(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
        declarationNode.setType(type);
        declarationNode.setToken(currToken);
        match(ID_MET_VAR);
        asignacionAttrAux(declarationNode, visibility, accessMode, type);

        if (!ST.getCurrClass().containsAttribute(attributeName)) {
            ST.getCurrClass().addAttribute(attribute);
        } else {
            throw buildSemanticException(attribute, "nombre de atributo duplicado");
        }
    }

    private void asignacionAttrAux(DeclarationNode declarationNode, IVisibility visibility, IAccessMode accessMode, IType type) throws SyntaxException, LexicalException, SemanticException {
        if (equalsAny(ASSIGN)) {
            AccessVariableNode accessNode = new AccessVariableNode(declarationNode.getLine(), declarationNode.getRow(), declarationNode.getColumn());
            accessNode.setToken(declarationNode.getToken());
            AssignmentNode assignmentNode = new AssignmentNode(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            assignmentNode.setLeftSide(accessNode);
            assignmentNode.setToken(currToken);
            match(ASSIGN);
            assignmentNode.setRightSide(expresion());
            SymbolTable.getInstance().getCurrClass().addAttributeAssignment(assignmentNode);
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
        ST.getCurrMethod().setAbstractSyntaxTree(bloque());
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
            IClassType classType = new TypeClass(typeClassName, fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
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
        TypePrimitive primitiveType;
        if (equalsAny(PR_BOOLEAN)) {
            primitiveType = new TypeBoolean(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            match(PR_BOOLEAN);

        } else if (equalsAny(PR_CHAR)) {
            primitiveType = new TypeChar(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            match(PR_CHAR);

        } else if (equalsAny(PR_INT)) {
            primitiveType = new TypeInt(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            match(PR_INT);

        } else if (equalsAny(PR_STRING)) {
            primitiveType = new TypeString(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
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
            outType = new TypeVoid(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
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

    private CodeBlockNode bloque() throws SyntaxException {
        CodeBlockNode codeBlockNode = new CodeBlockNode(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
        codeBlockNode.setToken(currToken);
        match(BRACES_OPEN);

        List<SentenceNode> sentenceNodeList = null;
        try {
            sentenceNodeList = listaSentencias();
        } catch (CompilerException e) {
            saveException(e);
            updateTokenUntilSentinel(BRACES_CLOSE);
        }
        if (sentenceNodeList != null) {
            codeBlockNode.getSentences().addAll(sentenceNodeList);
        }
        match(BRACES_CLOSE);
        return codeBlockNode;
    }

    private List<SentenceNode> listaSentencias() throws SyntaxException {
        List<SentenceNode> sentenceNodeList = new ArrayList<>();

        if (equalsAny(SEMICOLON, STATIC, IF, WHILE, RETURN, ID_CLASS, BRACES_OPEN, PARENTHESES_OPEN, PR_BOOLEAN, PR_CHAR, PR_INT, PR_STRING, THIS, NEW, ID_MET_VAR)) {

            try {
                sentenceNodeList.addAll(sentencia());
            } catch (CompilerException e) {
                saveException(e);
                updateTokenUntilSentinel(SEMICOLON, BRACES_OPEN, BRACES_CLOSE);
            }

            sentenceNodeList.addAll(listaSentencias());
        } else if (!equalsAny(BRACES_CLOSE)) {
            throw buildSyntaxException("una sentencia o }");
        }
        return sentenceNodeList;
    }

    private List<SentenceNode> sentencia() throws LexicalException, SyntaxException, SemanticException {
        List<SentenceNode> out = new ArrayList<>();
        if (equalsAny(SEMICOLON)) {
            SentenceNode emptySentenceNode = new EmptySentenceNode(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            emptySentenceNode.setToken(currToken);
            out.add(emptySentenceNode);

            match(SEMICOLON);
        } else if (equalsAny(PARENTHESES_OPEN, THIS, NEW, ID_MET_VAR)) {
            ExpressionNode accessNode = acceso();
            out.add(sentenciaAUX(accessNode));
            match(SEMICOLON);
        } else if (equalsAny(STATIC)) {
            match(STATIC);
            AccessStaticNode staticAccessNode = new AccessStaticNode(fileHandler.getCurrentLine(), fileHandler.getRow(), formaMetodo().getColumn());
            String classTypeName = currToken.getLexeme();
            IClassType classType = new TypeClass(classTypeName, fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            staticAccessNode.setClassType(classType);
            match(ID_CLASS);

            // TODO
            genericidad();
            // GenericityNode genericityNode = new GenericityNode(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());

            out.add(preAccesoEstatico(staticAccessNode));

            match(SEMICOLON);
        } else if (equalsAny(PR_BOOLEAN, PR_CHAR, PR_INT, PR_STRING)) {
            IType type = tipoPrimitivo();
            out.addAll(asignacionVariableLocal(type));

            match(SEMICOLON);
        } else if (equalsAny(ID_CLASS)) {
            String classTypeName = currToken.getLexeme();
            IClassType classType = new TypeClass(classTypeName, fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());

            match(ID_CLASS);

            IClassType genericClass = genericidad();
            classType.setGenericType(genericClass);

            out.addAll(accesoEstaticoODeclaracion(classType));
            match(SEMICOLON);
        } else if (equalsAny(IF)) {
            IfNode ifNode = new IfNode(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            ifNode.setToken(currToken);
            match(IF);
            match(PARENTHESES_OPEN);

            ifNode.setCondition(expresion());
            match(PARENTHESES_CLOSE);

            CodeBlockNode body = new CodeBlockNode(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            body.getSentences().addAll(sentencia());
            ifNode.setBody(body);

            ifNode.setElseNode(sentenciaAUX1());
            out.add(ifNode);
        } else if (equalsAny(WHILE)) {
            WhileNode whileNode = new WhileNode(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            whileNode.setToken(currToken);
            match(WHILE);
            match(PARENTHESES_OPEN);
            whileNode.setCondition(expresion());
            match(PARENTHESES_CLOSE);
            CodeBlockNode body = new CodeBlockNode(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            body.getSentences().addAll(sentencia());
            whileNode.setBody(body);

            out.add(whileNode);
        } else if (equalsAny(BRACES_OPEN)) {
            out.add(bloque());
        } else if (equalsAny(RETURN)) {
            ReturnNode returnNode = new ReturnNode(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            returnNode.setToken(currToken);
            match(RETURN);
            returnNode.setExpressionNode(expresionOVacio());
            out.add(returnNode);

            match(SEMICOLON);
        } else {
            throw buildSyntaxException("comienzo de una sentencia");
        }
        return out;
    }

    private SentenceNode sentenciaAUX(ExpressionNode accessNode) throws LexicalException, SyntaxException {
        if (equalsAny(ASSIGN, ASSIGN_ADD, ASSIGN_SUB)) {
            return asignacion(accessNode);
        } else if (!equalsAny(SEMICOLON)) {
            throw buildSyntaxException(";");
        }

        AccessSentenceNode out = new AccessSentenceNode(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
        out.setExpressionNode(accessNode);
        return out;
    }

    private ElseNode sentenciaAUX1() throws LexicalException, SyntaxException, SemanticException {
        if (equalsAny(ELSE)) {
            ElseNode elseNode = new ElseNode(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            elseNode.setToken(currToken);
            match(ELSE);
            CodeBlockNode body = new CodeBlockNode(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            body.getSentences().addAll(sentencia());
            elseNode.setBody(body);
            return elseNode;
        } else if (!equalsAny(SEMICOLON, IF, WHILE, RETURN, BRACES_OPEN, PARENTHESES_OPEN, PR_BOOLEAN, PR_CHAR, PR_INT, PR_STRING, THIS, NEW, ID_MET_VAR, ELSE, BRACES_CLOSE)) {
            throw buildSyntaxException("; if while return idclase { ( boolean char int string this static new idmetvar else }");
        }
        return null;
    }

    private List<SentenceNode> accesoEstaticoODeclaracion(IType classType) throws SyntaxException, LexicalException, SemanticException {
        if (equalsAny(ID_MET_VAR)) {
            return asignacionVariableLocal(classType);
        } else if (equalsAny(DOT)) {
            AccessStaticNode accessStaticNode = new AccessStaticNode(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            accessStaticNode.setClassType(classType);
            return Collections.singletonList(preAccesoEstatico(accessStaticNode));
        }
        throw buildSyntaxException("idMetVar o .");
    }

    private AssignmentNode asignacion(ExpressionNode accessNode) throws LexicalException, SyntaxException {
        AssignmentNode assignmentNode = tipoDeAsignacion();

        assignmentNode.setLeftSide(accessNode);
        assignmentNode.setRightSide(expresion());

        return assignmentNode;
    }

    private AssignmentNode tipoDeAsignacion() throws SyntaxException {
        AssignmentNode out = new AssignmentNode(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
        out.setToken(currToken);
        if (equalsAny(ASSIGN)) {
            match(ASSIGN);
        } else if (equalsAny(ASSIGN_SUB)) {
            match(ASSIGN_SUB);
        } else if (equalsAny(ASSIGN_ADD)) {
            match(ASSIGN_ADD);
        } else {
            throw buildSyntaxException("= += -=");
        }
        return out;
    }

    private List<SentenceNode> asignacionVariableLocal(IType type) throws SyntaxException, LexicalException, SemanticException {
        DeclarationNode declarationNode = new DeclarationNode(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
        declarationNode.setType(type);
        declarationNode.setToken(currToken);
        match(ID_MET_VAR);
        List<SentenceNode> out = asignacionVarAux(declarationNode, type);
        out.add(0, declarationNode);

        return out;
    }

    private List<SentenceNode> asignacionVarAux(DeclarationNode declarationNode, IType type) throws SyntaxException, LexicalException, SemanticException {
        List<SentenceNode> out = new ArrayList<>();

        if (equalsAny(ASSIGN)) {
            AccessVariableNode accessNode = new AccessVariableNode(declarationNode.getLine(), declarationNode.getRow(), declarationNode.getColumn());
            accessNode.setToken(declarationNode.getToken());
            AssignmentNode assignmentNode = new AssignmentNode(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            assignmentNode.setLeftSide(accessNode);
            assignmentNode.setToken(currToken);
            match(ASSIGN);
            assignmentNode.setRightSide(expresion());
            out.add(assignmentNode);
        }

        out.addAll(listaAsignacionVar(type));
        return out;
    }

    private List<SentenceNode> listaAsignacionVar(IType type) throws SyntaxException, LexicalException, SemanticException {
        if (equalsAny(COMMA)) {
            match(COMMA);
            return asignacionVariableLocal(type);
        } else if (!equalsAny(SEMICOLON)) {
            throw buildSyntaxException("nombre o asignacion de variables");
        }
        return new ArrayList<>();
    }

    private ExpressionNode expresionOVacio() throws LexicalException, SyntaxException {
        if (equalsAny(ADD, SUB, OP_NOT, NULL, ID_CLASS, BOOLEAN, INTEGER, CHARACTER, STRING, PARENTHESES_OPEN, THIS, NEW, ID_MET_VAR)) {
            return expresion();
        } else if (!equalsAny(SEMICOLON)) {
            throw buildSyntaxException("valor o ;");
        }
        return null;
    }

    private ExpressionNode expresion() throws LexicalException, SyntaxException {
        return or();
    }

    private ExpressionNode or() throws SyntaxException, LexicalException {
        ExpressionNode leftSide = and();

        ExpressionBinaryNode expressionBinaryNode = orAux();
        if (expressionBinaryNode != null) {
            expressionBinaryNode.setLeftSide(leftSide);
            return expressionBinaryNode;
        }
        return leftSide;
    }

    private ExpressionBinaryNode orAux() throws SyntaxException, LexicalException {
        if (equalsAny(OP_OR)) {
            ExpressionBinaryNode expressionBinaryNode = op1();
            expressionBinaryNode.setRightSide(and());
            ExpressionBinaryNode expressionNode = orAux();
            if (expressionNode != null) {
                expressionNode.setLeftSide(expressionBinaryNode);
                return expressionNode;
            }
            return expressionBinaryNode;
        } else if (!equalsAny(PARENTHESES_CLOSE, COMMA, SEMICOLON)) {
            throw buildSyntaxException("|| ) , ;");
        }
        return null;
    }

    private ExpressionNode and() throws SyntaxException, LexicalException {
        ExpressionNode leftSide = equalsExp();

        ExpressionBinaryNode expressionBinaryNode = andAux();
        if (expressionBinaryNode != null) {
            expressionBinaryNode.setLeftSide(leftSide);
            return expressionBinaryNode;
        }
        return leftSide;
    }

    private ExpressionBinaryNode andAux() throws SyntaxException, LexicalException {
        if (equalsAny(OP_AND)) {
            ExpressionBinaryNode expressionBinaryNode = op2();
            expressionBinaryNode.setToken(currToken);

            expressionBinaryNode.setRightSide(equalsExp());
            ExpressionBinaryNode expressionNode = andAux();
            if (expressionNode != null) {
                expressionNode.setLeftSide(expressionBinaryNode);
                return expressionNode;
            }
            return expressionBinaryNode;
        } else if (!equalsAny(OP_OR, PARENTHESES_CLOSE, COMMA, SEMICOLON)) {
            throw buildSyntaxException("&& || ) , ;");
        }
        return null;
    }

    private ExpressionNode equalsExp() throws SyntaxException, LexicalException {
        ExpressionNode leftSide = inEq();

        ExpressionBinaryNode expressionBinaryNode = equalsAux();
        if (expressionBinaryNode != null) {
            expressionBinaryNode.setLeftSide(leftSide);
            return expressionBinaryNode;
        }
        return leftSide;
    }

    private ExpressionBinaryNode equalsAux() throws SyntaxException, LexicalException {
        if (equalsAny(EQUALS, NOT_EQUALS)) {
            ExpressionBinaryNode expressionBinaryNode = op3();

            expressionBinaryNode.setRightSide(inEq());
            ExpressionBinaryNode expressionNode = equalsAux();
            if (expressionNode != null) {
                expressionNode.setLeftSide(expressionBinaryNode);
                return expressionNode;
            }
            return expressionBinaryNode;
        } else if (!equalsAny(OP_AND, OP_OR, PARENTHESES_CLOSE, COMMA, SEMICOLON)) {
            throw buildSyntaxException("== != && || ) , ;");
        }
        return null;
    }

    private ExpressionNode inEq() throws SyntaxException, LexicalException {
        ExpressionNode leftSide = add();

        ExpressionBinaryNode expressionBinaryNode = inEqAux();
        if (expressionBinaryNode != null) {
            expressionBinaryNode.setLeftSide(leftSide);
            return expressionBinaryNode;
        }
        return leftSide;
    }

    private ExpressionBinaryNode inEqAux() throws SyntaxException, LexicalException {
        if (equalsAny(LESS_THAN, GREATER_THAN, LESS_EQUALS, GREATER_EQUALS)) {
            ExpressionBinaryNode expressionBinaryNode = op4();

            expressionBinaryNode.setRightSide(add());
            ExpressionBinaryNode expressionNode = inEqAux();
            if (expressionNode != null) {
                expressionNode.setLeftSide(expressionBinaryNode);
                return expressionNode;
            }
            return expressionBinaryNode;
        } else if (!equalsAny(EQUALS, NOT_EQUALS, OP_AND, OP_OR, PARENTHESES_CLOSE, COMMA, SEMICOLON)) {
            throw buildSyntaxException("operador binario");
        }
        return null;
    }

    private ExpressionNode add() throws SyntaxException, LexicalException {
        ExpressionNode leftSide = mult();

        ExpressionBinaryNode expressionBinaryNode = addAux();
        if (expressionBinaryNode != null) {
            expressionBinaryNode.setLeftSide(leftSide);
            return expressionBinaryNode;
        }
        return leftSide;
    }

    private ExpressionBinaryNode addAux() throws SyntaxException, LexicalException {
        if (equalsAny(ADD, SUB)) {
            ExpressionBinaryNode expressionBinaryNode = op5();

            expressionBinaryNode.setRightSide(mult());
            ExpressionBinaryNode expressionNode = addAux();
            if (expressionNode != null) {
                expressionNode.setLeftSide(expressionBinaryNode);
                return expressionNode;
            }
            return expressionBinaryNode;
        } else if (!equalsAny(LESS_THAN, GREATER_THAN, LESS_EQUALS, GREATER_EQUALS, EQUALS, NOT_EQUALS, OP_AND, OP_OR, PARENTHESES_CLOSE, COMMA, SEMICOLON)) {
            throw buildSyntaxException("|| ) , ;");
        }
        return null;
    }

    private ExpressionNode mult() throws SyntaxException, LexicalException {
        ExpressionNode leftSide = expresionUnaria();

        ExpressionBinaryNode expressionBinaryNode = multAux();
        if (expressionBinaryNode != null) {
            expressionBinaryNode.setLeftSide(leftSide);
            return expressionBinaryNode;
        }
        return leftSide;
    }

    private ExpressionBinaryNode multAux() throws SyntaxException, LexicalException {
        if (equalsAny(MULTIPLY, DIVIDE, REMAINDER)) {
            ExpressionBinaryNode expressionBinaryNode = op6();

            expressionBinaryNode.setRightSide(expresionUnaria());
            ExpressionBinaryNode expressionNode = multAux();
            if (expressionNode != null) {
                expressionNode.setLeftSide(expressionBinaryNode);
                return expressionNode;
            }
            return expressionBinaryNode;
        } else if (!equalsAny(ADD, SUB, LESS_THAN, GREATER_THAN, LESS_EQUALS, GREATER_EQUALS, EQUALS, NOT_EQUALS, OP_AND, OP_OR, PARENTHESES_CLOSE, COMMA, SEMICOLON)) {
            throw buildSyntaxException("multiplicacion, suma, in/ecuacion, operador booleano ) , ;");
        }
        return null;
    }

    private ExpressionBinaryNode op1() throws SyntaxException {
        ExpressionBinaryNode out = new ExpressionBinaryBetweenBooleans(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
        out.setToken(currToken);
        match(OP_OR);
        return out;
    }

    private ExpressionBinaryNode op2() throws SyntaxException {
        ExpressionBinaryNode out = new ExpressionBinaryBetweenBooleans(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
        out.setToken(currToken);
        match(OP_AND);
        return out;
    }

    private ExpressionBinaryNode op3() throws SyntaxException {
        ExpressionBinaryNode out = new ExpressionBinaryEqualsNode(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
        out.setToken(currToken);
        if (equalsAny(EQUALS)) {
            match(EQUALS);
        } else if (equalsAny(NOT_EQUALS)) {
            match(NOT_EQUALS);
        } else {
            throw buildSyntaxException("== o !=");
        }
        return out;
    }


    private ExpressionBinaryNode op4() throws SyntaxException {
        ExpressionBinaryNode out = new ExpressionBinaryBetweenIntegers(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
        out.setToken(currToken);
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
        return out;
    }

    private ExpressionBinaryNode op5() throws SyntaxException {
        ExpressionBinaryNode out = new ExpressionBinaryIntegerNode(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
        out.setToken(currToken);
        if (equalsAny(ADD)) {
            match(ADD);
        } else if (equalsAny(SUB)) {
            match(SUB);
        } else {
            throw buildSyntaxException("+ o -");
        }
        return out;
    }

    private ExpressionBinaryNode op6() throws SyntaxException {
        ExpressionBinaryNode out = new ExpressionBinaryIntegerNode(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
        out.setToken(currToken);
        if (equalsAny(MULTIPLY)) {
            match(MULTIPLY);
        } else if (equalsAny(DIVIDE)) {
            match(DIVIDE);
        } else if (equalsAny(REMAINDER)) {
            match(REMAINDER);
        } else {
            throw buildSyntaxException("*, / o %");
        }
        return out;
    }

    private ExpressionNode expresionUnaria() throws LexicalException, SyntaxException {
        ExpressionNode out;
        if (equalsAny(ADD, SUB, OP_NOT)) {
            ExpressionUnaryNode expressionUnaryNode = operadorUnario();
            expressionUnaryNode.setOperandNode(operando());
            out = expressionUnaryNode;
        } else if (equalsAny(NULL, BOOLEAN, INTEGER, CHARACTER, STRING, PARENTHESES_OPEN, ID_CLASS, THIS, NEW, ID_MET_VAR, STATIC)) {
            out = operando();
        } else {
            throw buildSyntaxException("una expresion u operando");
        }
        return out;
    }

    private ExpressionUnaryNode operadorUnario() throws SyntaxException {
        ExpressionUnaryNode out;
        if (equalsAny(ADD)) {
            out = new ExpressionUnaryIntegerNode(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            out.setToken(currToken);
            match(ADD);
        } else if (equalsAny(SUB)) {
            out = new ExpressionUnaryIntegerNode(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            out.setToken(currToken);
            match(SUB);
        } else if (equalsAny(OP_NOT)) {
            out = new ExpressionUnaryBooleanNode(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            out.setToken(currToken);
            match(OP_NOT);
        } else {
            throw buildSyntaxException("+ - !");
        }
        return out;
    }

    private ExpressionNode operando() throws LexicalException, SyntaxException {
        if (equalsAny(NULL, BOOLEAN, INTEGER, CHARACTER, STRING)) {
            return literal();
        } else if (equalsAny(STATIC, ID_CLASS, PARENTHESES_OPEN, THIS, NEW, ID_MET_VAR)) {
            return accesoOperando();
        }
        throw buildSyntaxException("literal o modo de acceso");
    }

    private TypeNode literal() throws SyntaxException {
        TypeNode out;
        IToken typeToken = currToken;
        if (equalsAny(NULL)) {
            out = new TypeNullNode(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            match(NULL);
        } else if (equalsAny(BOOLEAN)) {
            out = new TypeBooleanNode(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            match(BOOLEAN);
        } else if (equalsAny(INTEGER)) {
            out = new TypeIntNode(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            match(INTEGER);
        } else if (equalsAny(CHARACTER)) {
            out = new TypeCharNode(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn() - 2);
            match(CHARACTER);
        } else if (equalsAny(STRING)) {
            out = new TypeStringNode(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn() - 2);
            match(STRING);
        } else {
            throw buildSyntaxException("tipo primitivo o null");
        }
        out.setToken(typeToken);
        return out;
    }

    private SentenceNode preAccesoEstatico(AccessNode staticAccessNode) throws SyntaxException, LexicalException {
        ChainedNode chainedNode = varOMetodoEncadenado();
        staticAccessNode.setChainedNode(chainedNode);

        ChainedNode chainedNode1 = encadenado();
        if (chainedNode1 != null) {
            chainedNode.setChainedNode(chainedNode1);
        }

        return sentenciaAUX(staticAccessNode);
    }

    private ExpressionNode acceso() throws LexicalException, SyntaxException {
        ExpressionNode accessNode = primario();
        ChainedNode chainedNode = encadenado();
        if (accessNode.getChainedNode() != null) {
            getLastChainedNode(accessNode.getChainedNode()).setChainedNode(chainedNode);
        } else {
            accessNode.setChainedNode(chainedNode);
        }

        return accessNode;
    }

    private ExpressionNode accesoOperando() throws SyntaxException, LexicalException {
        if (equalsAny(PARENTHESES_OPEN, THIS, NEW, ID_MET_VAR)) {
            return acceso();
        } else if (equalsAny(STATIC, ID_CLASS)) {
            AccessNode accessNode = accesoEstatico();
            ChainedNode chainedNode = encadenado();
            if (accessNode.getChainedNode() != null) {
                getLastChainedNode(accessNode.getChainedNode()).setChainedNode(chainedNode);
            } else {
                accessNode.setChainedNode(chainedNode);
            }
            return accessNode;
        }
        throw buildSyntaxException("tipo de acceso");
    }

    private ChainedNode getLastChainedNode(ChainedNode chainedNode) {
        ChainedNode current = chainedNode;
        while (current != null && current.getChainedNode() != null) {
            current = current.getChainedNode();
        }
        return current;
    }

    private ExpressionNode primario() throws LexicalException, SyntaxException {
        ExpressionNode out;
        if (equalsAny(THIS)) {
            out = accesoThis();
        } else if (equalsAny(NEW)) {
            out = accesoConstructor();
        } else if (equalsAny(ID_MET_VAR)) {
            out = accesoVarOMetodo();
        } else if (equalsAny(PARENTHESES_OPEN)) {
            match(PARENTHESES_OPEN);
            out = expresion();
            match(PARENTHESES_CLOSE);
        } else {
            throw buildSyntaxException("acceso primario");
        }
        return out;
    }

    private AccessNode accesoThis() throws SyntaxException {
        if (equalsAny(THIS)) {
            AccessNode out = new AccessThisNode(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            out.setToken(currToken);
            match(THIS);
            return out;
        }
        throw buildSyntaxException("this");
    }

    private AccessNode accesoVarOMetodo() throws SyntaxException, LexicalException {
        if (equalsAny(ID_MET_VAR)) {
            String line = fileHandler.getCurrentLine();
            int row = fileHandler.getRow(), column = fileHandler.getColumn();
            IToken nameToken = currToken;
            match(ID_MET_VAR);
            AccessNode out = accesoVarOMetodoAUX(line, row, column);
            out.setToken(nameToken);
            return out;
        }
        throw buildSyntaxException("id met var");
    }

    private AccessNode accesoVarOMetodoAUX(String line, int row, int column) throws SyntaxException, LexicalException {
        if (equalsAny(PARENTHESES_OPEN)) {
            AccessMethodNode accessMethodNode = new AccessMethodNode(line, row, column);
            accessMethodNode.getActualParameters().addAll(argsActuales());
            return accessMethodNode;
        } else if (!equalsAny(SEMICOLON, DOT, EQUALS, NOT_EQUALS, LESS_THAN, GREATER_THAN, LESS_EQUALS, GREATER_EQUALS, ADD, SUB, MULTIPLY, DIVIDE, REMAINDER, OP_AND, OP_OR, PARENTHESES_CLOSE, COMMA, ASSIGN, ASSIGN_ADD, ASSIGN_SUB)) {
            throw buildSyntaxException("una expresion o )");
        }
        return new AccessVariableNode(line, row, column);
    }

    private AccessNode accesoEstatico() throws SyntaxException, LexicalException {
        AccessStaticNode out;
        if (equalsAny(STATIC)) {
            match(STATIC);
        } else if (!equalsAny(ID_CLASS)) {
            throw buildSyntaxException("static o idClase");
        }

        out = new AccessStaticNode(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
        out.setToken(currToken);

        String classTypeName = currToken.getLexeme();
        IClassType classType = new TypeClass(classTypeName, fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
        out.setClassType(classType);

        match(ID_CLASS);


        ChainedNode chainedNode = varOMetodoEncadenado();
        out.setChainedNode(chainedNode);

        ChainedNode chainedNode1 = encadenado();
        if (chainedNode1 != null) {
            chainedNode.setChainedNode(chainedNode1);
        }

        return out;
    }

    private AccessNode accesoConstructor() throws LexicalException, SyntaxException {
        if (equalsAny(NEW)) {

            match(NEW);
            AccessConstructorNode out = new AccessConstructorNode(fileHandler.getCurrentLine(), fileHandler.getRow(), fileHandler.getColumn());
            out.setToken(currToken);
            match(ID_CLASS);

            //TODO
            IClassType genericidad = genericidadImplicita();
            out.getActualParameters().addAll(argsActuales());
            return out;
        }
        throw buildSyntaxException("new");
    }

    private List<ExpressionNode> argsActuales() throws LexicalException, SyntaxException {
        match(PARENTHESES_OPEN);
        List<ExpressionNode> out = listaExpsOVacio();
        match(PARENTHESES_CLOSE);
        return out;
    }

    private List<ExpressionNode> listaExpsOVacio() throws LexicalException, SyntaxException {
        if (equalsAny(ADD, SUB, OP_NOT, NULL, BOOLEAN, INTEGER, CHARACTER, STRING, PARENTHESES_OPEN, THIS, ID_CLASS, NEW, ID_MET_VAR)) {
            return listaExps();
        } else if (!equalsAny(PARENTHESES_CLOSE)) {
            throw buildSyntaxException(")");
        }
        return new ArrayList<>();
    }

    private List<ExpressionNode> listaExps() throws LexicalException, SyntaxException {
        List<ExpressionNode> out = new ArrayList<>();
        try {
            out.add(expresion());
        } catch (CompilerException e) {
            saveException(e);
            updateTokenUntilSentinel(COMMA, PARENTHESES_CLOSE);
        }
        List<ExpressionNode> aux = listaExpsAUX();
        if (aux != null) {
            out.addAll(aux);
        }
        return out;
    }

    private List<ExpressionNode> listaExpsAUX() throws LexicalException, SyntaxException {
        if (equalsAny(COMMA)) {
            match(COMMA);
            return listaExps();
        } else if (!equalsAny(PARENTHESES_CLOSE)) {
            throw buildSyntaxException(") u otra expresion");
        }
        return null;
    }

    private ChainedNode encadenado() throws LexicalException, SyntaxException {
        ChainedNode out = null;
        if (equalsAny(DOT)) {
            out = varOMetodoEncadenado();
            ChainedNode chainedNode = encadenado();
            if (chainedNode != null) {
                out.setChainedNode(chainedNode);
            }
        } else if (!equalsAny(MULTIPLY, DIVIDE, REMAINDER, ADD, SUB, LESS_THAN, GREATER_THAN, LESS_EQUALS, GREATER_EQUALS, EQUALS, NOT_EQUALS, OP_AND, OP_OR, ASSIGN, ASSIGN_ADD, ASSIGN_SUB, PARENTHESES_CLOSE, COMMA, SEMICOLON)) {
            throw buildSyntaxException(". asignacion operacion binaria ; ");
        }
        return out;
    }

    private ChainedNode varOMetodoEncadenado() throws LexicalException, SyntaxException {
        match(DOT);
        String line = fileHandler.getCurrentLine();
        int column = fileHandler.getColumn();
        IToken idToken = currToken;
        match(ID_MET_VAR);
        return varOMetodoEncadenadoAUX(idToken, line, column);
    }

    private ChainedNode varOMetodoEncadenadoAUX(IToken idToken, String line, int column) throws LexicalException, SyntaxException {
        if (equalsAny(PARENTHESES_OPEN)) {
            ChainedMethodNode out = new ChainedMethodNode(line, idToken.getRowNumber(), column);
            out.getActualParameters().addAll(argsActuales());
            out.setToken(idToken);
            return out;
        } else if (!equalsAny(DOT, MULTIPLY, DIVIDE, REMAINDER, ADD, SUB, LESS_THAN, GREATER_THAN, LESS_EQUALS, GREATER_EQUALS, EQUALS, NOT_EQUALS, OP_AND, OP_OR, ASSIGN, ASSIGN_ADD, ASSIGN_SUB, PARENTHESES_CLOSE, COMMA, SEMICOLON)) {
            throw buildSyntaxException("metodo encadenado, asignacion u operador");
        }
        ChainedVariableNode out = new ChainedVariableNode(line, idToken.getRowNumber(), column);
        out.setToken(idToken);
        return out;
    }
}
