import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class ModuloPrincipalSemanticTest {

    static Stream<Arguments> correctos() {
        return Stream.of(
                Arguments.of("C:\\Users\\ezegi\\Desktop\\casos\\attributos.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\casos\\claseHerenciaCircular.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\casos\\constructor.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\casos\\genericidad.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\casos\\genericInterfaceError.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\casos\\genericInterfaceTest.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\casos\\herenciaAtributos.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\casos\\herenciadeMetodosAbuelo.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\casos\\herenciaDoble.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\casos\\herenciaError.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\casos\\herenciaMetodosClase.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\casos\\herenciaMetodosClase2.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\casos\\herenciaMetodosIaI.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\casos\\interfazHerenciaSinGenericidad.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\casos\\parametros.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\casos\\redefinicionEntreInterfaces.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\casos\\tipoGenerico.java"),
                Arguments.of("C:\\Users\\ezegi\\Desktop\\casos\\tipoGenerico2.java")
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