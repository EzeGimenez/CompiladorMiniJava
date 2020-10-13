package syntax_analyzer;

import lexical_analyzer.*;

import java.util.ArrayList;
import java.util.List;

import static lexical_analyzer.TokenDescriptor.*;

public class SyntaxAnalyzer implements ISyntaxAnalyzer {

    private final ILexicalAnalyzer lexicalAnalyzer;
    private final FileHandler fileHandler;
    private final List<Exception> exceptionList;
    private IToken currToken;

    public SyntaxAnalyzer(FileHandler fileHandler, ILexicalAnalyzer lexicalAnalyzer) {
        this.fileHandler = fileHandler;
        this.lexicalAnalyzer = lexicalAnalyzer;
        this.exceptionList = new ArrayList<>();
    }

    @Override
    public void start() {
        inicial();
    }

    @Override
    public void validate() throws Exception {
        if (exceptionList.size() > 0) {
            Exception e = exceptionList.get(0);
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

    private void match(TokenDescriptor expectedDescriptor) {
        if (expectedDescriptor != currToken.getDescriptor()) {
            saveException(buildException(expectedDescriptor.toString()));
        }
        updateToken();
    }

    private void updateTokenUntilAfterSentinel(TokenDescriptor... sentinels) {
        boolean halt = false;
        while (!halt) {
            for (TokenDescriptor d : sentinels) {
                if (d == currToken.getDescriptor()) {
                    halt = true;
                    break;
                }
            }
            if (currToken.getDescriptor() == END_OF_FILE) {
                halt = true;
            }
            if (!halt) {
                updateToken();
            }
        }
    }

    private SyntaxException buildException(String expected) {
        String message = getDisplayableMessage();

        return new SyntaxException(
                fileHandler.getCurrentLine(),
                expected,
                message,
                fileHandler.getRow(),
                fileHandler.getColumn());
    }

    private String getDisplayableMessage() {
        String out = currToken.getLexeme();

        switch (currToken.getDescriptor()) {
            case END_OF_FILE:
                out = "fin de archivo";
                break;
        }

        return out;
    }

    private void saveException(Exception e) {
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
            if (equalsAny(CLASS, INTERFACE)) {
                listaClases();
                match(END_OF_FILE);
            } else if (equalsAny(END_OF_FILE)) {
                match(END_OF_FILE);
            } else {
                saveException(buildException("clase o EOF"));
            }
        } catch (Exception e) {
            saveException(e);
        }
    }

    private void listaClases() throws LexicalException {
        try {
            if (equalsAny(CLASS)) {
                clase();
            } else if (equalsAny(INTERFACE)) {
                interfaz();
            } else {
                throw buildException("clase o interfaz");
            }
        } catch (Exception e) {
            saveException(e);
            updateTokenUntilAfterSentinel();
        }
        listaClasesAux();
    }

    private void listaClasesAux() throws LexicalException {
        if (equalsAny(CLASS)) {
            listaClases();
        } else if (!equalsAny(END_OF_FILE)) {
            saveException(buildException("una clase, interfaz o fin de archivo"));
        }
    }

    private void clase() throws SyntaxException {
        match(CLASS);
        match(ID_CLASS);
        genericidad();
        herencia();
        implementa();
        match(BRACES_OPEN);
        listaMiembros();
        match(BRACES_CLOSE);
    }

    private void interfaz() throws SyntaxException, LexicalException {
        match(INTERFACE);
        match(ID_CLASS);
        herenciaInterfaz();
        match(BRACES_OPEN);
        listaMiembrosInterfaz();
        match(BRACES_CLOSE);
    }

    private void genericidad() throws SyntaxException {
        if (equalsAny(LESS_THAN)) {
            match(LESS_THAN);
            match(ID_CLASS);
            match(GREATER_THAN);
        } else if (!equalsAny(EXTENDS, IMPLEMENTS, BRACES_OPEN)) {
            throw buildException("genericidad, extends, implements o {");
        } // nada
    }

    private void herencia() throws SyntaxException {
        if (equalsAny(EXTENDS)) {
            match(EXTENDS);
            match(ID_CLASS);
        } else if (!equalsAny(IMPLEMENTS, BRACES_OPEN)) {
            throw buildException("extends, implements o {");
        }  // nada
    }

    private void implementa() throws SyntaxException {
        if (equalsAny(IMPLEMENTS)) {
            match(IMPLEMENTS);
            listaInterfaces();
        } else if (!equalsAny(BRACES_OPEN)) {
            throw buildException("implements o {");
        } // nada
    }

    private void listaInterfaces() throws SyntaxException {
        match(ID_CLASS);
        listaInterfacesAux();
    }

    private void listaInterfacesAux() throws SyntaxException {
        if (equalsAny(COMMA)) {
            match(COMMA);
            listaInterfaces();
        } else if (!equalsAny(BRACES_OPEN)) {
            throw buildException("coma o {");
        } // nada
    }

    private void herenciaInterfaz() throws SyntaxException {
        if (equalsAny(EXTENDS)) {
            match(EXTENDS);
            listaInterfaces();
        } else if (!equalsAny(BRACES_OPEN)) {
            throw buildException("extends o {");
        } // nada
    }

    private void listaMiembros() throws SyntaxException {
        if (equalsAny(PUBLIC, PROTECTED, PRIVATE, ID_CLASS, STATIC, DYNAMIC, PR_BOOLEAN, PR_CHAR, PR_INT, PR_STRING)) {
            try {
                miembro();
            } catch (Exception e) {
                saveException(e);
                updateTokenUntilAfterSentinel(SEMICOLON, ID_CLASS, PUBLIC, PROTECTED, PRIVATE, STATIC, DYNAMIC, BRACES_CLOSE);
            }
            listaMiembros();
        } else if (!equalsAny(BRACES_CLOSE)) {
            throw buildException("constructor, metodo, atributo o }");
        }  // nada
    }

    private void listaMiembrosInterfaz() throws SyntaxException, LexicalException {
        if (equalsAny(STATIC, DYNAMIC)) {
            metodo();
            listaMiembrosInterfaz();
        } else if (!equalsAny(BRACES_CLOSE)) {
            throw buildException("metodo o }");
        } // nada
    }

    private void miembro() throws LexicalException, SyntaxException {
        if (equalsAny(PUBLIC, PROTECTED, PRIVATE, ID_CLASS, PR_STRING, PR_BOOLEAN, PR_CHAR, PR_INT)) {
            attrVisibilidad();
        } else if (equalsAny(STATIC, DYNAMIC)) {
            metodo();
        } else {
            throw buildException("metodo, constructor o atributo");
        }
    }

    private void attrVisibilidad() throws SyntaxException, LexicalException {
        if (equalsAny(PUBLIC)) {
            match(PUBLIC);
            attr();
        } else if (equalsAny(PROTECTED)) {
            match(PROTECTED);
            attr();
        } else if (equalsAny(PRIVATE)) {
            match(PRIVATE);
            attr();
        } else if (equalsAny(ID_CLASS, PR_BOOLEAN, PR_CHAR, PR_INT, PR_STRING)) {
            auxAttrOCons();
        }
    }

    private void attr() throws SyntaxException, LexicalException {
        estaticoOVacio();
        tipo();
        asignacionAttr();
        match(SEMICOLON);
    }

    private void estaticoOVacio() throws SyntaxException {
        if (equalsAny(STATIC)) {
            match(STATIC);
        } else if (!equalsAny(ID_CLASS, PR_BOOLEAN, PR_INT, PR_STRING)) {
            throw buildException("estatico o tipo");
        }
    }

    private void auxAttrOCons() throws SyntaxException, LexicalException {
        if (equalsAny(ID_CLASS)) {
            match(ID_CLASS);
            constructorOAttr();
        } else if (equalsAny(PR_BOOLEAN, PR_CHAR, PR_INT, PR_STRING)) {
            tipoPrimitivo();
            asignacionAttr();
        } else {
            throw buildException("tipo");
        }
    }

    private void constructorOAttr() throws SyntaxException, LexicalException {
        if (equalsAny(PARENTHESES_OPEN)) {
            constructor();
        } else if (equalsAny(ID_MET_VAR)) {
            asignacionAttr();
        } else {
            throw buildException("( o id var o metodo");
        }
    }

    private void constructor() throws SyntaxException, LexicalException {
        argsFormales();
        bloque();
    }

    private void asignacionAttr() throws SyntaxException, LexicalException {
        match(ID_MET_VAR);
        asignacionAttrAux();
    }

    private void asignacionAttrAux() throws SyntaxException, LexicalException {
        if (equalsAny(ASSIGN)) {
            match(ASSIGN);
            expresion();
            listaAsignacion();
        } else if (equalsAny(COMMA)) {
            listaAsignacion();
            match(SEMICOLON);
        } else if (!equalsAny(SEMICOLON)) {
            throw buildException("nombre atributos");
        } // nada
    }

    private void listaAsignacion() throws SyntaxException, LexicalException {
        if (equalsAny(COMMA)) {
            match(COMMA);
            asignacionAttr();
        } else if (!equalsAny(SEMICOLON)) {
            throw buildException("nombre o asignacion de variables");
        } // nada
    }

    private void metodo() throws LexicalException, SyntaxException {
        formaMetodo();
        tipoMetodo();
        match(ID_MET_VAR);
        argsFormales();
        bloque();
    }

    private void tipo() throws SyntaxException {
        if (equalsAny(PR_BOOLEAN, PR_CHAR, PR_INT, PR_STRING)) {
            tipoPrimitivo();
        } else if (equalsAny(ID_CLASS)) {
            match(ID_CLASS);
        } else {
            throw buildException("una definicion de tipo");
        }
    }

    private void tipoPrimitivo() throws SyntaxException {
        if (equalsAny(PR_BOOLEAN)) {
            match(PR_BOOLEAN);
        } else if (equalsAny(PR_CHAR)) {
            match(PR_CHAR);
        } else if (equalsAny(PR_INT)) {
            match(PR_INT);
        } else if (equalsAny(PR_STRING)) {
            match(PR_STRING);
        } else {
            throw buildException("boolean, char, int o String");
        }
    }

    private void formaMetodo() throws SyntaxException {
        if (equalsAny(STATIC)) {
            match(STATIC);
        } else if (equalsAny(DYNAMIC)) {
            match(DYNAMIC);
        } else {
            throw buildException("static o dynamic");
        }
    }

    private void tipoMetodo() throws SyntaxException {
        if (equalsAny(ID_CLASS, PR_BOOLEAN, PR_CHAR, PR_INT, PR_STRING)) {
            tipo();
        } else if (equalsAny(VOID)) {
            match(VOID);
        } else {
            throw buildException("idClass boolean char int String");
        }
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
            throw buildException(")");
        }  // nada
    }

    private void listaArgsFormales() throws LexicalException, SyntaxException {
        try {
            argFormal();
        } catch (Exception e) {
            saveException(e);
            updateTokenUntilAfterSentinel(COMMA, PARENTHESES_CLOSE);
        }
        listaArgsFormalesAUX();
    }

    private void listaArgsFormalesAUX() throws LexicalException, SyntaxException {
        if (equalsAny(COMMA)) {
            match(COMMA);
            listaArgsFormales();
        } else if (!equalsAny(PARENTHESES_CLOSE)) {
            throw buildException("otro argumento o )");
        }  // nada
    }

    private void argFormal() throws SyntaxException {
        tipo();
        match(ID_MET_VAR);
    }

    private void bloque() {
        match(BRACES_OPEN);
        try {
            listaSentencias();
        } catch (Exception e) {
            saveException(e);
        }
        match(BRACES_CLOSE);
    }

    private void listaSentencias() throws SyntaxException {
        if (equalsAny(SEMICOLON, IF, WHILE, RETURN, ID_CLASS, BRACES_OPEN, PARENTHESES_OPEN, PR_BOOLEAN, PR_CHAR, PR_INT, PR_STRING, THIS, NEW, ID_MET_VAR)) {

            try {
                sentencia();
            } catch (Exception e) {
                saveException(e);
                updateTokenUntilAfterSentinel(SEMICOLON, BRACES_CLOSE);
            }

            listaSentencias();
        } else if (!equalsAny(BRACES_CLOSE)) {
            throw buildException("una sentencia o }");
        }  // nada
    }

    private void sentencia() throws LexicalException, SyntaxException {
        if (equalsAny(SEMICOLON)) {
            match(SEMICOLON);
        } else if (equalsAny(PARENTHESES_OPEN, THIS, NEW, ID_MET_VAR)) {
            acceso();
            sentenciaAUX();
            match(SEMICOLON);
        } else if (equalsAny(PR_BOOLEAN, PR_CHAR, PR_INT, PR_STRING)) {
            tipoPrimitivo();
            listaDecVars();
            match(SEMICOLON);
        } else if (equalsAny(ID_CLASS)) {
            match(ID_CLASS);
            accesoEstaticoODeclaracion();
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
            throw buildException("; ( this static new idMetVar idClass boolean char int string if while { return");
        }
    }

    private void sentenciaAUX() throws LexicalException, SyntaxException {
        if (equalsAny(ASSIGN, ASSIGN_ADD, ASSIGN_SUB)) {
            asignacion();
        } else if (!equalsAny(SEMICOLON)) {
            throw buildException(";");
        } // nada
    }

    private void sentenciaAUX1() throws LexicalException, SyntaxException {
        if (equalsAny(ELSE)) {
            match(ELSE);
            sentencia();
        } else if (!equalsAny(SEMICOLON, IF, WHILE, RETURN, BRACES_OPEN, PARENTHESES_OPEN, PR_BOOLEAN, PR_CHAR, PR_INT, PR_STRING, THIS, NEW, ID_MET_VAR, ELSE, BRACES_CLOSE)) {
            throw buildException("; if while return idclase { ( boolean char int string this static new idmetvar else }");
        } // nada
    }

    private void accesoEstaticoODeclaracion() throws SyntaxException, LexicalException {
        if (equalsAny(ID_MET_VAR)) {
            listaDecVars();
        } else if (equalsAny(DOT)) {
            match(DOT);
            preAccesoEstatico();
        } else {
            throw buildException("idMetVar o .");
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
            throw buildException("= += -=");
        }
    }

    private void listaDecVars() throws LexicalException, SyntaxException {
        try {
            match(ID_MET_VAR);
        } catch (Exception e) {
            saveException(e);
            updateTokenUntilAfterSentinel(COMMA, SEMICOLON);
        }
        listaDecVarsAUX();
    }

    private void listaDecVarsAUX() throws LexicalException, SyntaxException {
        if (equalsAny(COMMA)) {
            match(COMMA);
            listaDecVars();
        } else if (!equalsAny(SEMICOLON)) {
            throw buildException(", ;");
        } // nada
    }

    private void expresionOVacio() throws LexicalException, SyntaxException {
        if (equalsAny(ADD, SUB, OP_NOT, NULL, ID_CLASS, BOOLEAN, INTEGER, CHARACTER, STRING, PARENTHESES_OPEN, THIS, NEW, ID_MET_VAR)) {
            expresion();
        } else if (!equalsAny(SEMICOLON)) {
            throw buildException("valor o ;");
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
            throw buildException("|| ) , ;");
        } // nada
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
            throw buildException("&& || ) , ;");
        } // nada
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
        } else if (!equalsAny(OP_AND, PARENTHESES_CLOSE, COMMA, SEMICOLON)) {
            throw buildException("&& || ) , ;");
        } // nada
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
            throw buildException("operador binario");
        } // nada
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
            throw buildException("|| ) , ;");
        } // nada
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
            throw buildException("|| ) , ;");
        } // nada
    }

    private void op1() {
        match(OP_OR);
    }

    private void op2() {
        match(OP_AND);
    }

    private void op3() throws SyntaxException {
        if (equalsAny(EQUALS)) {
            match(EQUALS);
        } else if (equalsAny(NOT_EQUALS)) {
            match(NOT_EQUALS);
        } else {
            throw buildException("== o !=");
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
            throw buildException("< > <= >=");
        }
    }

    private void op5() throws SyntaxException {
        if (equalsAny(ADD)) {
            match(ADD);
        } else if (equalsAny(SUB)) {
            match(SUB);
        } else {
            throw buildException("+ o -");
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
            throw buildException("*, / o %");
        }
    }

    private void expresionUnaria() throws LexicalException, SyntaxException {
        if (equalsAny(ADD, SUB, OP_NOT)) {
            operadorUnario();
            operando();
        } else if (equalsAny(NULL, BOOLEAN, INTEGER, CHARACTER, STRING, PARENTHESES_OPEN, ID_CLASS, THIS, NEW, ID_MET_VAR)) {
            operando();
        } else {
            throw buildException("una expresion unaria u operando");
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
            throw buildException("+ - !");
        }
    }

    private void operando() throws LexicalException, SyntaxException {
        if (equalsAny(NULL, BOOLEAN, INTEGER, CHARACTER, STRING)) {
            literal();
        } else if (equalsAny(PARENTHESES_OPEN, ID_CLASS, THIS, NEW, ID_MET_VAR)) {
            accesoOperando();
        } else {
            throw buildException("literal o modo de acceso");
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
            throw buildException("tipo primitivo o null");
        }
    }

    private void preAccesoEstatico() throws SyntaxException, LexicalException {
        accesoEstatico();
        encadenado();
        sentenciaAUX();
    }

    private void accesoOperando() throws SyntaxException, LexicalException {
        if (equalsAny(PARENTHESES_OPEN, THIS, NEW, ID_MET_VAR)) {
            acceso();
        } else if (equalsAny(ID_CLASS)) {
            accesoEstatico();
            encadenado();
        } else {
            throw buildException("tipo de acceso");
        }
    }

    private void acceso() throws LexicalException, SyntaxException {
        primario();
        encadenado();
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
            throw buildException("acceso primario");
        }
    }

    private void accesoThis() throws SyntaxException {
        if (equalsAny(THIS)) {
            match(THIS);
        } else {
            throw buildException("this");
        }
    }

    private void accesoVarOMetodo() throws SyntaxException, LexicalException {
        if (equalsAny(ID_MET_VAR)) {
            match(ID_MET_VAR);
            accesoVarOMetodoAUX();
        } else {
            throw buildException("id met var");
        }
    }

    private void accesoVarOMetodoAUX() throws SyntaxException, LexicalException {
        if (equalsAny(PARENTHESES_OPEN)) {
            argsActuales();
        } else if (!equalsAny(SEMICOLON, DOT, EQUALS, NOT_EQUALS, LESS_THAN, GREATER_THAN, LESS_EQUALS, GREATER_EQUALS, ADD, SUB, MULTIPLY, DIVIDE, REMAINDER, OP_AND, OP_OR, PARENTHESES_CLOSE, COMMA, ASSIGN, ASSIGN_ADD, ASSIGN_SUB)) {
            throw buildException("acceso var, asignacion u operador");
        } // nada
    }

    private void accesoEstatico() throws LexicalException, SyntaxException {
        if (equalsAny(ID_CLASS)) {
            match(ID_CLASS);
            match(DOT);
            accesoEstaticoMetodoOVar();
        } else {
            throw buildException("id de Clase");
        }
    }

    private void accesoConstructor() throws LexicalException, SyntaxException {
        if (equalsAny(NEW)) {
            match(NEW);
            match(ID_CLASS);
            argsActuales();
        } else {
            throw buildException("new");
        }
    }

    private void accesoEstaticoMetodoOVar() throws LexicalException, SyntaxException {
        if (equalsAny(ID_MET_VAR)) {
            match(ID_MET_VAR);
            accesoEstaticoMetodoOVarAux();
        } else {
            throw buildException("idMetVar");
        }
    }

    private void accesoEstaticoMetodoOVarAux() throws SyntaxException, LexicalException {
        if (equalsAny(PARENTHESES_OPEN)) {
            argsActuales();
        } else if (!equalsAny(ASSIGN, ASSIGN_ADD, ASSIGN_SUB, DOT, MULTIPLY, DIVIDE, REMAINDER, ADD, SUB, LESS_THAN, GREATER_THAN, LESS_EQUALS, GREATER_EQUALS, EQUALS, NOT_EQUALS, OP_AND, OP_OR, PARENTHESES_CLOSE, COMMA, SEMICOLON)) {
            throw buildException("( o operador binario, acceso");
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
            throw buildException(")");
        }// nada
    }

    private void listaExps() throws LexicalException, SyntaxException {
        try {
            expresion();
        } catch (Exception e) {
            saveException(e);
            updateTokenUntilAfterSentinel(COMMA, PARENTHESES_CLOSE);
        }
        listaExpsAUX();
    }

    private void listaExpsAUX() throws LexicalException, SyntaxException {
        if (equalsAny(COMMA)) {
            match(COMMA);
            listaExps();
        } else if (!equalsAny(PARENTHESES_CLOSE)) {
            throw buildException(") u otra expresion");
        } // nada
    }

    private void encadenado() throws LexicalException, SyntaxException {
        if (equalsAny(DOT)) {
            varOMetodoEncadenado();
            encadenado();
        } else if (!equalsAny(MULTIPLY, DIVIDE, REMAINDER, ADD, SUB, LESS_THAN, GREATER_THAN, LESS_EQUALS, GREATER_EQUALS, EQUALS, NOT_EQUALS, OP_AND, OP_OR, ASSIGN, ASSIGN_ADD, ASSIGN_SUB, PARENTHESES_CLOSE, COMMA, SEMICOLON)) {
            throw buildException(". asignacion operacion binaria ; ");
        } // nada
    }

    private void varOMetodoEncadenado() throws LexicalException, SyntaxException {
        try {
            match(DOT);
            match(ID_MET_VAR);
        } catch (Exception e) {
            saveException(e);
            updateTokenUntilAfterSentinel(PARENTHESES_OPEN, SEMICOLON);
        }
        varOMetodoEncadenadoAUX();
    }

    private void varOMetodoEncadenadoAUX() throws LexicalException, SyntaxException {
        if (equalsAny(PARENTHESES_OPEN)) {
            argsActuales();
        } else if (!equalsAny(MULTIPLY, DIVIDE, REMAINDER, ADD, SUB, LESS_THAN, GREATER_THAN, LESS_EQUALS, GREATER_EQUALS, EQUALS, NOT_EQUALS, OP_AND, OP_OR, ASSIGN, ASSIGN_ADD, ASSIGN_SUB, PARENTHESES_CLOSE, COMMA, SEMICOLON)) {
            throw buildException("metodo encadenado, asignacion u operador");
        } // nada
    }
}
