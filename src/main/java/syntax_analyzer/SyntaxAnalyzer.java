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

    //TODO decir que son los "// nada" -> forma avanzada de pre deteccion con siguientes de...

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

    private void match(TokenDescriptor expectedDescriptor) throws SyntaxException {
        if (expectedDescriptor != currToken.getDescriptor()) {
            throw buildException(expectedDescriptor.toString());
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
            case EOF:
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
                match(EOF);
            } else {
                saveException(buildException("clase, interfaz o fin de archivo"));
            }
        } catch (Exception e) {
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
                throw buildException("clase o interfaz");
            }
        } catch (Exception e) {
            saveException(e);
            updateTokenUntilSentinel();
        }
        listaClasesAux();
    }

    private void listaClasesAux() throws LexicalException, SyntaxException {
        if (equalsAny(CLASS, INTERFACE)) {
            listaClases();
        } else if (!equalsAny(EOF)) {
            throw buildException("una clase, interfaz o fin de archivo");
        }
    }

    private void clase() throws SyntaxException {
        try {
            match(CLASS);
            match(ID_CLASS);
            genericidad();
            herencia();
            implementa();
        } catch (SyntaxException e) {
            saveException(e);
            updateTokenUntilSentinel(BRACES_OPEN);
        }
        match(BRACES_OPEN);
        listaMiembros();
        match(BRACES_CLOSE);
    }

    private void interfaz() throws SyntaxException {
        try {
            match(INTERFACE);
            match(ID_CLASS);
            genericidad();
            herenciaInterfaz();
        } catch (SyntaxException e) {
            saveException(e);
            updateTokenUntilSentinel(BRACES_OPEN);
        }
        match(BRACES_OPEN);
        listaMiembrosInterfaz();
        match(BRACES_CLOSE);
    }

    private void genericidad() throws SyntaxException {
        if (equalsAny(LESS_THAN)) {
            match(LESS_THAN);
            match(ID_CLASS);
            genericidad();
            match(GREATER_THAN);
        } else if (!equalsAny(EXTENDS, IMPLEMENTS, BRACES_OPEN, GREATER_THAN, COMMA, ID_MET_VAR, DOT, ASSIGN, ASSIGN_ADD, ASSIGN_SUB, SEMICOLON, PARENTHESES_OPEN)) {
            throw buildException("token siguiente a genericidad");
        } // nada
    }

    private void herencia() throws SyntaxException {
        if (equalsAny(EXTENDS)) {
            match(EXTENDS);
            match(ID_CLASS);
            genericidad();
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
        genericidad();
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
                updateTokenUntilSentinel(ID_CLASS, PUBLIC, PROTECTED, PRIVATE, STATIC, DYNAMIC, BRACES_CLOSE);
            }
            listaMiembros();
        } else if (!equalsAny(BRACES_CLOSE)) {
            throw buildException("constructor, metodo, atributo o }");
        }  // nada
    }

    private void listaMiembrosInterfaz() throws SyntaxException {
        if (equalsAny(STATIC, DYNAMIC)) {
            try {
                metodoDeclaracion();
            } catch (Exception e) {
                saveException(e);
                updateTokenUntilSentinel(SEMICOLON, ID_CLASS, PUBLIC, PROTECTED, PRIVATE, STATIC, DYNAMIC, BRACES_CLOSE);
            }
            listaMiembrosInterfaz();
        } else if (!equalsAny(BRACES_CLOSE)) {
            throw buildException("metodo o }");
        } // nada
    }

    private void miembro() throws LexicalException, SyntaxException {
        if (equalsAny(PUBLIC, PROTECTED, PRIVATE, ID_CLASS, PR_STRING, PR_BOOLEAN, PR_CHAR, PR_INT)) {
            attrVisibilidad();
        } else if (equalsAny(STATIC, DYNAMIC)) {
            metodoConCuerpo();
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
        } else if (!equalsAny(ID_CLASS, PR_BOOLEAN, PR_CHAR, PR_INT, PR_STRING)) {
            throw buildException("static o tipo");
        } // nada
    }

    private void auxAttrOCons() throws SyntaxException, LexicalException {
        if (equalsAny(ID_CLASS)) {
            match(ID_CLASS);
            genericidad();
            constructorOAttr();
        } else if (equalsAny(PR_BOOLEAN, PR_CHAR, PR_INT, PR_STRING)) {
            tipoPrimitivo();
            asignacionAttr();
            match(SEMICOLON);
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
        }
        listaAsignacion();
    }

    private void listaAsignacion() throws SyntaxException, LexicalException {
        if (equalsAny(COMMA)) {
            match(COMMA);
            asignacionAttr();
        } else if (!equalsAny(SEMICOLON)) {
            throw buildException("nombre o asignacion de variables");
        } // nada
    }

    private void cabeceraMetodo() throws LexicalException, SyntaxException {
        formaMetodo();
        tipoMetodo();
        match(ID_MET_VAR);
        argsFormales();
    }

    private void metodoConCuerpo() throws SyntaxException, LexicalException {
        cabeceraMetodo();
        bloque();
    }

    private void metodoDeclaracion() throws SyntaxException, LexicalException {
        cabeceraMetodo();
        match(SEMICOLON);
    }

    private void tipo() throws SyntaxException {
        if (equalsAny(PR_BOOLEAN, PR_CHAR, PR_INT, PR_STRING)) {
            tipoPrimitivo();
        } else if (equalsAny(ID_CLASS)) {
            match(ID_CLASS);
            genericidad();
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
            throw buildException("argumento o )");
        }  // nada
    }

    private void listaArgsFormales() throws LexicalException, SyntaxException {
        try {
            argFormal();
        } catch (Exception e) {
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
            throw buildException("otro argumento o )");
        }  // nada
    }

    private void argFormal() throws SyntaxException {
        tipo();
        match(ID_MET_VAR);
    }

    private void bloque() throws SyntaxException {
        match(BRACES_OPEN);
        try {
            listaSentencias();
        } catch (Exception e) {
            saveException(e);
            updateTokenUntilSentinel(BRACES_CLOSE);
        }
        match(BRACES_CLOSE);
    }

    private void listaSentencias() throws SyntaxException {
        if (equalsAny(SEMICOLON, STATIC, IF, WHILE, RETURN, ID_CLASS, BRACES_OPEN, PARENTHESES_OPEN, PR_BOOLEAN, PR_CHAR, PR_INT, PR_STRING, THIS, NEW, ID_MET_VAR)) {

            try {
                sentencia();
            } catch (Exception e) {
                saveException(e);
                updateTokenUntilSentinel(SEMICOLON, BRACES_OPEN, BRACES_CLOSE);
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
        } else if (equalsAny(STATIC)) {
            match(STATIC);
            match(ID_CLASS);
            genericidad();
            preAccesoEstatico();
            match(SEMICOLON);
        } else if (equalsAny(PR_BOOLEAN, PR_CHAR, PR_INT, PR_STRING)) {
            tipoPrimitivo();
            asignacionAttr();
            match(SEMICOLON);
        } else if (equalsAny(ID_CLASS)) {
            match(ID_CLASS);
            genericidad();
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
            throw buildException("comienzo de una sentencia");
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
            asignacionAttr();
        } else if (equalsAny(DOT)) {
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
        } else if (!equalsAny(OP_AND, OP_OR, PARENTHESES_CLOSE, COMMA, SEMICOLON)) {
            throw buildException("== != && || ) , ;");
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
            throw buildException("multiplicacion, suma, in/ecuacion, operador booleano ) , ;");
        } // nada
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
        } else if (equalsAny(NULL, BOOLEAN, INTEGER, CHARACTER, STRING, PARENTHESES_OPEN, ID_CLASS, THIS, NEW, ID_MET_VAR, STATIC)) {
            operando();
        } else {
            throw buildException("una expresion u operando");
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
        } else if (equalsAny(STATIC, ID_CLASS, PARENTHESES_OPEN, THIS, NEW, ID_MET_VAR)) {
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
            throw buildException("tipo de acceso");
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
            throw buildException("una expresion o )");
        } // nada
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
            throw buildException("static o idClase");
        }
    }

    private void accesoConstructor() throws LexicalException, SyntaxException {
        if (equalsAny(NEW)) {
            match(NEW);
            match(ID_CLASS);
            genericidad();
            argsActuales();
        } else {
            throw buildException("new");
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
        } // nada
    }

    private void listaExps() throws LexicalException, SyntaxException {
        try {
            expresion();
        } catch (Exception e) {
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
            updateTokenUntilSentinel(PARENTHESES_OPEN, SEMICOLON);
        }
        varOMetodoEncadenadoAUX();
    }

    private void varOMetodoEncadenadoAUX() throws LexicalException, SyntaxException {
        if (equalsAny(PARENTHESES_OPEN)) {
            argsActuales();
        } else if (!equalsAny(DOT, MULTIPLY, DIVIDE, REMAINDER, ADD, SUB, LESS_THAN, GREATER_THAN, LESS_EQUALS, GREATER_EQUALS, EQUALS, NOT_EQUALS, OP_AND, OP_OR, ASSIGN, ASSIGN_ADD, ASSIGN_SUB, PARENTHESES_CLOSE, COMMA, SEMICOLON)) {
            throw buildException("metodo encadenado, asignacion u operador");
        } // nada
    }
}
