import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class ModuloPrincipalSyntacticTest {

    static Stream<Arguments> correctos() {
        return Stream.of(
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Correctos\\Test1.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Correctos\\Test2.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Correctos\\Test3.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Correctos\\Test4.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Correctos\\Test5.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Correctos\\Test6.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Correctos\\Test7.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Correctos\\Test8.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Correctos\\Test9.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Correctos\\Test10.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Correctos\\Test11.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Correctos\\Test12.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Correctos\\Test13.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Correctos\\Test14.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Correctos\\Test15.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Correctos\\Test16.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Correctos\\Test17.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Correctos\\interfaz.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Correctos\\asignacionMultiple.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Correctos\\accesoEstatico.java")
        );
    }

    static Stream<Arguments> incorrectos() {
        return Stream.of(
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test1.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test2.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test3.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test4.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test5.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test6.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test7.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test8.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test9.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test10.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test11.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test12.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test13.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test14.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test15.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test16.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test17.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test18.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test19.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test20.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test21.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test22.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test23.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test24.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test25.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test26.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test27.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test28.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test29.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test30.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test31.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test32.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test33.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test34.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test35.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test36.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test37.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test38.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test39.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test40.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test41.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test42.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test43.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test44.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test45.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test46.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test47.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test48.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test49.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\Test49.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\accesoEstatico.java")
        );
    }

    static Stream<Arguments> test() {
        return Stream.of(
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Test0.java"));
    }

    @ParameterizedTest
    @MethodSource("test")
    void test(String filename) {
        new ModuloPrincipalSyntactic(filename);
    }

    @ParameterizedTest
    @MethodSource("incorrectos")
    void incorrecto(String filename) {
        new ModuloPrincipalSyntactic(filename);
    }

    @ParameterizedTest
    @MethodSource("correctos")
    void correcto(String filename) {
        new ModuloPrincipalSyntactic(filename);
    }
}