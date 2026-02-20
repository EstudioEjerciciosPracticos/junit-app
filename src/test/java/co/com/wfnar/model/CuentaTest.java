package co.com.wfnar.model;

import co.com.wfnar.DineroInsuficienteException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS) // se crea una instacia de la clase para todas las pruebas
class CuentaTest {

//    @BeforeAll : Se ejecuta antes de todos los metodos
//    @BeforeEach : Se ejecuta antes de cada metodo
//    @AfterAll : Se ejecuta despues de todos los metodos
//    @AfterEach : Se ejecuta despues de cada metodo

    Cuenta cuenta;
    private TestReporter testReporter;
    private TestInfo testInfo;
    @BeforeEach
    void initMetodoTest(TestInfo testInfo, TestReporter testReporter){
        this.cuenta = new Cuenta("Felipe", new BigDecimal("1000.123"));
        System.out.println("Iniciando Metodo");
/*        this.testInfo = testInfo;
        this.testReporter = testReporter;
        System.out.println("iniciando el metodo.");
        testReporter.publishEntry(" ejecutando: " + testInfo.getDisplayName() + " " + testInfo.getTestMethod().orElse(null).getName()
                + " con las etiquetas " + testInfo.getTags());*/
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

    @Test
    void testDebitoCuenta() {

        cuenta.debito(new BigDecimal("100"));
        BigDecimal expected = new BigDecimal("900.123");
        BigDecimal saldo = cuenta.getSaldo();

        assertNotNull(cuenta.getSaldo());
        assertEquals(saldo, expected);
        assertEquals("900.123", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testCreditoCuenta() {

        cuenta.credito(new BigDecimal("100"));
        BigDecimal expected = new BigDecimal("1100.123");
        BigDecimal saldo = cuenta.getSaldo();

        assertNotNull(cuenta.getSaldo());
        assertEquals(saldo, expected);
        assertEquals("1100.123", cuenta.getSaldo().toPlainString());
    }

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
    void testTransferirDineroCuenta() {
        Cuenta cuenta1 = new Cuenta("Felipe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("William", new BigDecimal("2500"));
        Banco banco = new Banco();
        banco.setNombre("Bancolombia");
        banco.transferir(cuenta2, cuenta1, new BigDecimal("500"));
        assertEquals("2000", cuenta2.getSaldo().toPlainString());
        assertEquals("3000", cuenta1.getSaldo().toPlainString());

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