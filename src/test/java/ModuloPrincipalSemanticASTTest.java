import exceptions.CompilerException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(Parameterized.class)
public class ModuloPrincipalSemanticASTTest {

    private static final String ABS_PATH = "C:\\Users\\ezegi\\Desktop\\casos";

    @Parameterized.Parameter
    public String fileName;

    @Rule
    public ErrorCollector collector = new ErrorCollector();

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

    @Test
    public void test() throws IOException {
        ModuloPrincipal p = new ModuloPrincipalSemanticBeautified(ABS_PATH + "\\" + fileName);
        //ModuloPrincipal p = new ModuloPrincipalSemantic(ABS_PATH + "\\" + fileName);

        List<CompilerException> compilerExceptionList = p.getCompilerExceptionList();
        List<String[]> mustHaveExceptions = getMustHaveExceptions(ABS_PATH + "\\" + fileName);

        collector.checkThat(mustHaveExceptions.size(), equalTo(compilerExceptionList.size()));
        for (CompilerException e : compilerExceptionList) {
            boolean found = false;
            for (String[] s : mustHaveExceptions) {
                if (areEquals(s, e)) {
                    found = true;
                    break;
                }
            }
            collector.checkThat("No encontrado:" + e.getLexeme() + "-" + e.getRow() + ":" + e.getMessage(),
                    found,
                    equalTo(true));
        }
    }

    private boolean areEquals(String[] s, CompilerException e) {
        return s[0].equals(e.getLexeme()) && Integer.parseInt(s[1]) == (e.getRow());
    }

    private List<String[]> getMustHaveExceptions(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        List<String[]> out = new ArrayList<>();

        String line;
        boolean cont = true;
        while (cont && (line = br.readLine()) != null) {
            if (line.startsWith("//")) {
                line = line.substring(2);
                String[] error = line.split("-");
                if (error.length > 1) {
                    out.add(error);
                }
            } else {
                cont = false;
            }
        }
        return out;
    }

}