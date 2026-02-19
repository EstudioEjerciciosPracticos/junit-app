package co.com.wfnar.model;

import co.com.wfnar.DineroInsuficienteException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {

    @Test
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
        Cuenta cuenta = new Cuenta("Felipe", new BigDecimal("1000.123"));
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
        Cuenta cuenta = new Cuenta("William", new BigDecimal("1000.123"));
        Cuenta cuenta2 = new Cuenta("William", new BigDecimal("1000.123"));

//        assertNotEquals(cuenta2, cuenta);
        assertEquals(cuenta2, cuenta);
    }

    @Test
    void testDebitoCuenta() {
        Cuenta cuenta = new Cuenta("William", new BigDecimal("1000.123"));

        cuenta.debito(new BigDecimal("100"));
        BigDecimal expected = new BigDecimal("900.123");
        BigDecimal saldo = cuenta.getSaldo();

        assertNotNull(cuenta.getSaldo());
        assertEquals(saldo, expected);
        assertEquals("900.123", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testCreditoCuenta() {
        Cuenta cuenta = new Cuenta("William", new BigDecimal("1000.123"));

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
    void testRelacionBancoCuenta2() {
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

}