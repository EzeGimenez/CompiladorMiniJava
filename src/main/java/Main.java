public class Main {

    public static void main(String[] args) {
        if (args.length == 2) {
            String fileName = args[0];
            String fileNameOut = args[1];
            new ModuloPrincipalCeIVM(fileName, fileNameOut);
        } else if (args.length == 3) {
            if (args[2].equals("-b")) {
                String fileName = args[0];
                String fileNameOut = args[1];
                new ModuloPrincipalCeIVM(fileName, fileNameOut);
            } else {
                System.out.println(args[1] + " no reconocido");
            }
        } else {
            System.out.println("Debe incluir el nombre del archivo en los argumentos del programa");
        }
    }
}
