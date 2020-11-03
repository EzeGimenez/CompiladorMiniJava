import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class ModuloPrincipalSemanticTest {

    static Stream<Arguments> correctos() {
        return Stream.of(

        );
    }

    static Stream<Arguments> incorrectos() {
        return Stream.of(

        );
    }

    static Stream<Arguments> test() {
        return Stream.of(
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Test0.java"));
    }

    @ParameterizedTest
    @MethodSource("test")
    void test(String filename) {
        new ModuloPrincipalSemantic(filename);
    }

    @ParameterizedTest
    @MethodSource("incorrectos")
    void incorrecto(String filename) {
        new ModuloPrincipalSemantic(filename);
    }

    @ParameterizedTest
    @MethodSource("correctos")
    void correcto(String filename) {
        new ModuloPrincipalSemantic(filename);
    }
}