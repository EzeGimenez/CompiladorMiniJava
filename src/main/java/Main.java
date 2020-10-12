public class Main {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Debe incluir el nombre del archivo en los argumentos del programa");
        } else {
            String fileName = args[0];
            ModuloPrincipal moduloPrincipal = new ModuloPrincipalSyntactic(fileName);
        }
    }
}
