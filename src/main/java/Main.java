public class Main {

    public static void main(String[] args) {
        if (args.length == 1) {
            String fileName = args[0];
            new ModuloPrincipalSemantic(fileName);
        } else if (args.length == 2) {
            if (args[1].equals("-b")) {
                String fileName = args[0];
                new ModuloPrincipalSemanticBeautified(fileName);
            } else {
                System.out.println(args[1] + " no reconocido");
            }
        } else {
            System.out.println("Debe incluir el nombre del archivo en los argumentos del programa");
        }
    }
}
