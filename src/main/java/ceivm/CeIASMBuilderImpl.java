package ceivm;

import semantic_analyzer.IClass;
import semantic_analyzer.SymbolTable;

public class CeIASMBuilderImpl implements CeIASMBuilder {

    public CeIASMBuilderImpl(String filePath) {
        InstructionWriter.getInstance().setFilePath(filePath);
    }

    @Override
    public void start() {
        init();

        generateCode();
        addPredefinedRoutines();

        InstructionWriter.getInstance().close();
    }

    private void generateCode() {
        for (IClass c : SymbolTable.getInstance().getClassMap().values()) {
            if (!c.getName().equals("System")) {
                SymbolTable.getInstance().setCurrClass(c);
                c.generateCode();
            }
        }
        generateSystemRoutines();
    }

    private void init() {
        IInstructionWriter writer = InstructionWriter.getInstance();

        writer.write("PUSH", "simple_heap_init");
        writer.write("CALL");

        writer.changeToDataSection();
        SymbolTable.getInstance().generateVTs();

        writer.changeToCodeSection();
        addMainMethod();

        writer.newLine();
        writer.write("HALT");
    }

    private void addMainMethod() {
        IInstructionWriter writer = InstructionWriter.getInstance();
        writer.write("push", "main");
        writer.write("call");
    }

    private void addPredefinedRoutines() {
        IInstructionWriter writer = InstructionWriter.getInstance();

        // simple heap init
        writer.addTag("simple_heap_init");
        writer.write("RET", 0, "Retorna inmediatamente");

        // simple malloc
        writer.addTag("simple_malloc");
        writer.write("LOADFP");
        writer.write("LOADSP");
        writer.write("STOREFP");
        writer.write("LOADHL");
        writer.write("DUP");
        writer.write("PUSH", 1);
        writer.write("ADD");
        writer.write("STORE", 4);
        writer.write("LOAD", 3);
        writer.write("ADD");
        writer.write("STOREHL");
        writer.write("STOREFP");
        writer.write("RET", 1);
    }

    private void generateSystemRoutines() {
        IInstructionWriter writer = InstructionWriter.getInstance();

        writer.addTag(TagProvider.getConstructorTag("System"));
        methodCallSetup();
        methodReturn(1);

        writer.addTag(TagProvider.getMethodTag("System", "read"));
        methodCallSetup();
        writer.write("read");
        writer.write("store", 3, "guardamos el valor leido en el espacio de retoro");
        methodReturn(0);

        writer.addTag(TagProvider.getMethodTag("System", "printB"));
        methodCallSetup();
        writer.write("load", 3);
        writer.write("bprint");
        methodReturn(1);

        writer.addTag(TagProvider.getMethodTag("System", "printBln"));
        methodCallSetup();
        writer.write("load", 3);
        writer.write("bprint");
        writer.write("prnln");
        methodReturn(1);

        writer.addTag(TagProvider.getMethodTag("System", "printC"));
        methodCallSetup();
        writer.write("load", 3);
        writer.write("cprint");
        methodReturn(1);

        writer.addTag(TagProvider.getMethodTag("System", "printCln"));
        methodCallSetup();
        writer.write("load", 3);
        writer.write("cprint");
        writer.write("prnln");
        methodReturn(1);

        writer.addTag(TagProvider.getMethodTag("System", "printI"));
        methodCallSetup();
        writer.write("load", 3);
        writer.write("iprint");
        methodReturn(1);

        writer.addTag(TagProvider.getMethodTag("System", "printIln"));
        methodCallSetup();
        writer.write("load", 3);
        writer.write("iprint");
        writer.write("prnln");
        methodReturn(1);

        writer.addTag(TagProvider.getMethodTag("System", "println"));
        methodCallSetup();
        writer.write("prnln");
        methodReturn(0);

        writer.addTag(TagProvider.getMethodTag("System", "printS"));
        methodCallSetup();
        writer.write("load", 3);
        writer.write("sprint");
        methodReturn(1);

        writer.addTag(TagProvider.getMethodTag("System", "printSln"));
        methodCallSetup();
        writer.write("load", 3);
        writer.write("sprint");
        writer.write("prnln");
        methodReturn(1);
    }

    private void methodCallSetup() {
        IInstructionWriter writer = InstructionWriter.getInstance();

        writer.write("LOADFP");
        writer.write("LOADSP");
        writer.write("STOREFP");
    }

    private void methodReturn(int parameterCount) {
        IInstructionWriter writer = InstructionWriter.getInstance();

        writer.write("storefp");
        writer.write("ret", parameterCount);
    }
}
