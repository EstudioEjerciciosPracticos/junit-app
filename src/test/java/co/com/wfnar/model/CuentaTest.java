package co.com.wfnar.model;

import co.com.wfnar.DineroInsuficienteException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS) // se crea una instacia de la clase para todas las pruebas
class CuentaTest {

//    @BeforeAll : Se ejecuta antes de todos los metodos
//    @BeforeEach : Se ejecuta antes de cada metodo
//    @AfterAll : Se ejecuta despues de todos los metodos
//    @AfterEach : Se ejecuta despues de cada metodo
//    @Tag parametrizar un grupo de pruebas

    Cuenta cuenta;
    private TestReporter testReporter;
    private TestInfo testInfo;

    @BeforeEach
    void initMetodoTest(TestInfo testInfo, TestReporter testReporter){
        this.cuenta = new Cuenta("Felipe", new BigDecimal("1000.123"));
        System.out.println("Iniciando Metodo");
        this.testInfo = testInfo;
        this.testReporter = testReporter;
        System.out.println("iniciando el metodo.");
        testReporter.publishEntry(" ejecutando: " + testInfo.getDisplayName() + " " + testInfo.getTestMethod().orElse(null).getName()
                + " con las etiquetas " + testInfo.getTags());
    }

    @AfterEach
    void tearDown() {
        System.out.println("Finalizando la prueba");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("Inicializando el test");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando el test");
    }

    @Tag("cuenta")
    @Nested
    class CuentaTestNombreSaldo{
        @Test
        @DisplayName("Probando el nombre de la cuenta") //Nombrar prueba
        void testNombreCuenta() {
            // Arrange: Preparar el entorno para pruebas. iniciar objetos creación datos de entrada creación de mocks (Dobles de pruebas)
            Cuenta cuenta = new Cuenta();
            cuenta.setPersona("Felipe");
            String esperado = "Felipe";

            // Action: Invocar metodo o función a probar
            String actual = cuenta.getPersona();

            // Assertions: Verificar que la acción reqalizada haya producido el resultadoesperado se comparan valores obtenidos con resultados.
            assertNotNull(actual, "La cuenta no puede ser nula");
            assertEquals(esperado, actual, "El nombre de la cuenta no es el que se esperaba");
            assertTrue(actual.equals("Felipe"), "Nombre de la cuenta esperada debe ser igual a la real");
        }

        @Test
        void testSaldoCuenta() {
            BigDecimal expected = new BigDecimal("1000.123");

            BigDecimal saldo = cuenta.getSaldo();

            assertNotNull(saldo);
            assertEquals(expected, saldo);
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);

        }

        // TEST DRIVEN DEVELOPMENT (TDD) Desarrollo Orientado a Pruebas


        @Test
        void testReferenciaCuentas() {
            cuenta = new Cuenta("William", new BigDecimal("1000.123"));
            Cuenta cuenta2 = new Cuenta("William", new BigDecimal("1000.123"));

//        assertNotEquals(cuenta2, cuenta);
            assertEquals(cuenta2, cuenta);
        }
    }

    @Nested
    class CuentaOperacionesTest{
        @Tag("cuenta")
        @Test
        void testDebitoCuenta() {

            cuenta.debito(new BigDecimal("100"));
            BigDecimal expected = new BigDecimal("900.123");
            BigDecimal saldo = cuenta.getSaldo();

            assertNotNull(cuenta.getSaldo());
            assertEquals(saldo, expected);
            assertEquals("900.123", cuenta.getSaldo().toPlainString());
        }

        @Tag("cuenta")
        @Test
        void testCreditoCuenta() {

            cuenta.credito(new BigDecimal("100"));
            BigDecimal expected = new BigDecimal("1100.123");
            BigDecimal saldo = cuenta.getSaldo();

            assertNotNull(cuenta.getSaldo());
            assertEquals(saldo, expected);
            assertEquals("1100.123", cuenta.getSaldo().toPlainString());
        }

        @Tag("cuenta")
        @Tag("banco")
        @Test
        void testTransferirDineroCuenta() {
            Cuenta cuenta1 = new Cuenta("Felipe", new BigDecimal("2500"));
            Cuenta cuenta2 = new Cuenta("William", new BigDecimal("2500"));
            Banco banco = new Banco();
            banco.setNombre("Bancolombia");
            banco.transferir(cuenta2, cuenta1, new BigDecimal("500"));
            assertEquals("2000", cuenta2.getSaldo().toPlainString());
            assertEquals("3000", cuenta1.getSaldo().toPlainString());

        }
    }

    @Tag("cuenta")
    @Tag("error")
    @Test
    void testDineroInsuficienteExceptionCuenta() {
        Cuenta cuenta = new Cuenta("Andres", new BigDecimal("1000.123"));
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal(1500));
        });
        String actual = exception.getMessage();
        String expect = "Dinero Insuficiente";

        assertEquals(expect, actual);
    }

    @Test
    void testRelacionBancoCuenta() {
        Cuenta cuenta1 = new Cuenta("Felipe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("William", new BigDecimal("2500"));

        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);

        banco.setNombre("Bancolombia");

        banco.transferir(cuenta2, cuenta1, new BigDecimal("500"));
        assertEquals("2000", cuenta2.getSaldo().toPlainString());
        assertEquals("3000", cuenta1.getSaldo().toPlainString());

        assertEquals(2, banco.getCuentas().size());
        assertEquals("Bancolombia", cuenta1.getBanco().getNombre());
        assertEquals("Felipe", banco.getCuentas()
                .stream()
                .filter(c -> c.getPersona().equals("Felipe"))
                .findFirst()
                .get()
                .getPersona());
        assertTrue(banco.getCuentas().stream()
                .anyMatch(c -> c.getPersona().equals("William")));

    }

    @Test
    @DisplayName("Prueba Relación entre cuentas y bancos")
    //@Disabled //Deshabilitar prueba
    void testRelacionBancoCuenta2() {
        //fail(); // Forzar error
        Cuenta cuenta1 = new Cuenta("Felipe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("William", new BigDecimal("2500"));

        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);

        banco.setNombre("Bancolombia");

        banco.transferir(cuenta2, cuenta1, new BigDecimal("500"));
        assertAll(
                () -> {
                    assertEquals("2000", cuenta2.getSaldo().toPlainString());
                },
                () -> {
                    assertEquals("3000", cuenta1.getSaldo().toPlainString());
                },
                () -> {
                    assertEquals(2, banco.getCuentas().size());
                },
                () -> {
                    assertEquals("Bancolombia", cuenta1.getBanco().getNombre());
                },
                () -> {
                    assertEquals("Felipe", banco.getCuentas().stream()
                            .filter(c -> c.getPersona().equals("Felipe"))
                            .findFirst()
                            .get()
                            .getPersona());
                },
                () -> {
                    assertTrue(banco.getCuentas().stream()
                            .anyMatch(c -> c.getPersona().equals("William")));
                }
        );
    }
    @Nested
    class SistemaOperativoTest{
        @Test
        @EnabledOnOs(OS.WINDOWS)
        void testSoloWindows() {
        }

        @Test
        @EnabledOnOs({OS.LINUX, OS.MAC})
        void testSoloLinuxMac() {
        }

        @Test
        @DisabledOnOs(OS.WINDOWS)
        void testNoWindows() {
        }

        @Test
        @DisabledOnOs({OS.LINUX, OS.MAC})
        void testNoLinuxMac() {
        }
    }

    @Nested
    class JavaVersionTest{
        @Test
        @EnabledOnJre(JRE.JAVA_25)
        void testSoloJdk25() {
        }

        @Test
        @EnabledOnJre(JRE.JAVA_17)
        void testSoloJdk17() {
        }

        @Test
        @DisabledOnJre(JRE.JAVA_25)
        void testNoJDK25() {
        }
    }

    @Nested
    class SistemPropertiesTest{
        @Test
        void imprimirSystemProperties() {
            Properties properties = System.getProperties();
            properties.forEach((k, v)-> System.out.println(k + ":" + v));
        }

        @Test
        @EnabledIfSystemProperty(named = "java.version", matches = ".*25.*")
        void testJavaVersion() {
        }

        @Test
        @DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        void testSolo64() {
        }

        @Test
        @EnabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        void testNO64() {
        }

        @Test
        @EnabledIfSystemProperty(named = "user.name", matches = "WilliamFelipeNaranjo")
        void testUsername() {
        }

        @Test
        @EnabledIfSystemProperty(named = "ENV", matches = "dev")
        void testDev() {
        }
    }

    @Nested
    class VariablesAmbienteTest{
        @Test
        void imprimirVariablesAmbiente() {
            Map<String, String> getenv = System.getenv();
            getenv.forEach((k, v)-> System.out.println(k + " = " + v));
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*jdk-25.0.2.*")
        void testJavaHome() {
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "8")
        void testProcesadores() {
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "dev")
        void testEnv() {
        }

        @Test
        @DisabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "prod")
        void testEnvProdDisabled() {
        }
    }



    @Test
    @DisplayName("test Saldo Cuenta Dev")
    void testSaldoCuentaDev() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumeTrue(esDev);
        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.123, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("test Saldo Cuenta Dev 2")
    void testSaldoCuentaDev2() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumingThat(esDev, () -> {
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.123, cuenta.getSaldo().doubleValue());
        });
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @DisplayName("Probando Debito Cuenta Repetir!")
    @RepeatedTest(value=5, name = "{displayName} - Repetición numero {currentRepetition} de {totalRepetitions}")
    void testDebitoCuentaRepetir() {

        cuenta.debito(new BigDecimal("100"));
        BigDecimal expected = new BigDecimal("900.123");
        BigDecimal saldo = cuenta.getSaldo();

        assertNotNull(cuenta.getSaldo());
        assertEquals(saldo, expected);
        assertEquals("900.123", cuenta.getSaldo().toPlainString());
    }

    @Tag("param")
    @Nested
    class PruebasParametrizadasTest{

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @ValueSource(strings = {"100", "200", "300", "500", "700", "1000.123"})
        void testDebitoCuentaValueSource(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"1,100", "2,200", "3,300", "4,500", "5,700", "6,1000.123"})
        void testDebitoCuentaCsvSource(String index, String monto) {
            System.out.println(index + " -> " + monto);
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"200,100,John,Andres", "250,200,Pepe,Pepe", "300,300,maria,Maria", "510,500,Pepa,Pepa", "750,700,Lucas,Luca", "1000.12345,1000.12345,Cata,Cata"})
        void testDebitoCuentaCsvSource2(String saldo, String monto, String esperado, String actual) {
            System.out.println(saldo + " -> " + monto);
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);

            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado, actual);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data.csv")
        void testDebitoCuentaCsvFileSource(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data2.csv")
        void testDebitoCuentaCsvFileSource2(String saldo, String monto, String esperado, String actual) {
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);

            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado, actual);

            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

    }

    @Tag("param")
    @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
    @MethodSource("montoList")
    void testDebitoCuentaMethodSource(String monto) {
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    static List<String> montoList() {
        return Arrays.asList("100", "200", "300", "500", "700", "1000.123");
    }


    @Nested
    @Tag("timeout")
    class EjemploTimeoutTest{
        @Test
        @Timeout(1)
        void pruebaTimeout() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(100);
        }

        @Test
        @Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
        void pruebaTimeout2() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(900);
        }

        @Test
        void testTimeoutAssertions() {
            assertTimeout(Duration.ofSeconds(5), ()->{
                TimeUnit.MILLISECONDS.sleep(4000);
            });
        }
    }


}