import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ModuloPrincipalSemanticTest {

    static Stream<Arguments> testCases() {
        File folder = new File("C:\\Users\\ezegi\\Desktop\\casos2\\logroInterfaces");
        File[] listOfFiles = folder.listFiles();
        List<Arguments> fileList = new ArrayList<>();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                fileList.add(Arguments.of(listOfFiles[i].getAbsolutePath()));
            }
        }
        return fileList.stream();
    }

    @ParameterizedTest
    @MethodSource("testCases")
    public void test(String filename) throws IOException {
        new ModuloPrincipalSemanticBautyfied(filename);
    }

    @ParameterizedTest
    @MethodSource("testCases")
    public void testOriginal(String filename) throws IOException {
        new ModuloPrincipalSemantic(filename);
    }
}