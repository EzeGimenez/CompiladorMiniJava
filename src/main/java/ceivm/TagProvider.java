package ceivm;

public class TagProvider {

    private static int elseTagAutoIncrement = 0;
    private static int ifexitTagAutoIncrement = 0;
    private static int whileTagAutoIncrement = 0;
    private static int whileExitTagAutoIncrement = 0;

    private synchronized static int nextElseInt() {
        return elseTagAutoIncrement++;
    }

    private synchronized static int nextIfExitInt() {
        return ifexitTagAutoIncrement++;
    }

    private synchronized static int nextWhileInt() {
        return whileTagAutoIncrement++;
    }

    private synchronized static int nextWhileExitInt() {
        return whileExitTagAutoIncrement++;
    }

    public static String getIfExitTag() {
        return "if_exit_" + nextIfExitInt();
    }

    public static String getElseTag() {
        return "else_" + nextElseInt();
    }

    public static String getWhileTag() {
        return "while_" + nextWhileInt();
    }

    public static String getWhileExitTag() {
        return "while_exit_" + nextWhileExitInt();
    }

    public static String getMethodTag(String holderClassName, String methodName) {
        return holderClassName + "_method_" + methodName;
    }

    public static String getConstructorTag(String holderClassName) {
        return holderClassName + "_constructor";
    }

    public static String getVtTag(String className) {
        return "vt_" + className;
    }

}
