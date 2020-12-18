import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.fail;

@RunWith(Parameterized.class)
public class ModuloPrincipalCEIVMTest {

    private static final String ABS_PATH = "C:\\D_Drive\\Eze\\UNS\\4.2 CUARENTENA 2.0\\Compiladores e Interpretes\\TestMiniJava-main\\Testing\\Etapa5";
    private static final String ABS_PATH_OUT = "C:\\D_Drive\\Eze\\UNS\\4.2 CUARENTENA 2.0\\Compiladores e Interpretes\\TestMiniJava-main\\Testing\\Etapa5 out";

    private static int currTestCase = 0;
    @Parameterized.Parameter
    public String fileName;
    @Rule
    public ErrorCollector collector = new ErrorCollector();
    private List<String> expectedStringList;

    @Parameterized.Parameters(name = "{0}")
    public static List<String> testCases() {
        File folder = new File(ABS_PATH);
        File[] listOfFiles = folder.listFiles();
        List<String> fileList;
        fileList = new ArrayList<>();
        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                fileList.add(listOfFile.getName());
            }
        }
        return fileList;
    }

    @Before
    public void before() {
        try {
            expectedStringList = new ArrayList<>();
            Scanner expectedScanner = new Scanner(new File(ABS_PATH + "\\" + "expected"));
            while (expectedScanner.hasNext()) {
                expectedStringList.add(expectedScanner.nextLine());
            }
            expectedScanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test() throws IOException {
        if (!fileName.equals("expected")) {
            ModuloPrincipal mp = new ModuloPrincipalCeIVMBeautified(ABS_PATH + "\\" + fileName, ABS_PATH_OUT + "\\" + fileName + "_code");
            if (mp.getCompilerExceptionList().size() == 0) {

                ProcessBuilder builder = new ProcessBuilder(
                        "java",
                        "-jar",
                        "\"C:\\D_Drive\\Eze\\UNS\\4.2 CUARENTENA 2.0\\Compiladores e Interpretes\\etapa 5\\CeIVM2011\\CeIVM-cei2011.jar\"",
                        "\"" + ABS_PATH_OUT + "\\" + fileName + "_code" + "\"");

                builder.redirectOutput(new File(ABS_PATH_OUT + "\\" + fileName + "_out"));
                Process process = builder.start();

                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
                out.write("1");
                out.flush();
                out.close();

                try {
                    process.waitFor();
                    compare();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                fail();
            }
        }
    }

    private void compare() {
        try {
            Scanner scanner = new Scanner(new BufferedReader(new FileReader(ABS_PATH_OUT + "\\" + fileName + "_out")));
            int lineCount = 0;
            String currLine;
            while (scanner.hasNext()) {
                currLine = scanner.nextLine();
                if (lineCount++ == 5) {
                    //collector.checkThat(currLine, equalTo(expectedStringList.get(currTestCase)));
                }
            }
            if (lineCount <= 5) {
                //fail();
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        currTestCase++;
    }

}