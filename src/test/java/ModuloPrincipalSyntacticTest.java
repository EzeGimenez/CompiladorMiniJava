import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class ModuloPrincipalSyntacticTest {

    static Stream<Arguments> correctos() {
        return Stream.of(
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Correctos\\interfaz.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Correctos\\asignacionMultiple.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Correctos\\accesoEstatico.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Correctos\\constructores.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Correctos\\declaracionInicializacion.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Correctos\\dobleParentesis.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Correctos\\general.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Correctos\\herenciaClase.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Correctos\\herenciaInterfaz.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Correctos\\ifAnidado.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Correctos\\sentenciasVarias.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Correctos\\variasClases.java")
        );
    }

    static Stream<Arguments> incorrectos() {
        return Stream.of(
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\accesoEstatico.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\clasesMalFormadas.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\Incorrectos\\archivoVacio.java")
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