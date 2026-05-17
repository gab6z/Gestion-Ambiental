import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.Date;
import java.sql.Time;
import modelo.Iniciativa;
import controlador.IniciativaControlador;

/**
 * Clase de pruebas unitarias para los métodos de validación del controlador
 * IniciativaControlador del sistema EcoVida.
 * Cubre los métodos validarFechasYTiempos() y validarPresupuestoYLogistica()
 * mediante casos correctos y de error.
 *
 * @author Solis Caballero Geovanny Andrés
 * @version 1.0
 */
public class IniciativaControladorTest {

    private IniciativaControlador controlador;
    private Iniciativa ini;

    /**
     * Inicializa el controlador sin panel (null) ya que los métodos
     * a probar no dependen de la UI, y prepara un objeto Iniciativa
     * base con datos válidos para cada test.
     */
    @BeforeEach
    public void setUp() {
        controlador = new IniciativaControlador(null, true);
        ini = new Iniciativa();
    }

    // =========================================================
    // TESTS PARA validarFechasYTiempos()
    // =========================================================

    /**
     * T1 - CORRECTO
     * Fecha de fin posterior a fecha de ejecución.
     * No debe lanzar ninguna excepción.
     */
    @Test
    public void testFechaFinPosteriorAEjecucion_correcto() {
        ini.setFechaEjecucion(Date.valueOf("2026-06-01"));
        ini.setFechaFin(Date.valueOf("2026-07-01"));

        assertDoesNotThrow(() -> controlador.validarFechasYTiempos(ini));
    }

    /**
     * T2 - ERROR
     * Fecha de fin anterior a fecha de ejecución.
     * Debe lanzar IllegalArgumentException con el mensaje correspondiente.
     */
    @Test
    public void testFechaFinAnteriorAEjecucion_error() {
        ini.setFechaEjecucion(Date.valueOf("2026-06-01"));
        ini.setFechaFin(Date.valueOf("2026-05-01"));

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> controlador.validarFechasYTiempos(ini)
        );
        assertEquals("La fecha de finalización no puede ser anterior a la de ejecución.", ex.getMessage());
    }

    /**
     * T3 - CORRECTO
     * Hora de inicio antes que hora de fin.
     * No debe lanzar ninguna excepción.
     */
    @Test
    public void testHoraInicioAntesQueFin_correcto() {
        ini.setHoraInicio(Time.valueOf("08:00:00"));
        ini.setHoraFin(Time.valueOf("10:00:00"));

        assertDoesNotThrow(() -> controlador.validarFechasYTiempos(ini));
    }

    /**
     * T4 - ERROR
     * Hora de inicio igual a hora de fin.
     * Debe lanzar IllegalArgumentException con el mensaje correspondiente.
     */
    @Test
    public void testHoraInicioIgualAFin_error() {
        ini.setHoraInicio(Time.valueOf("10:00:00"));
        ini.setHoraFin(Time.valueOf("10:00:00"));

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> controlador.validarFechasYTiempos(ini)
        );
        assertEquals("La hora de inicio debe ser anterior a la hora de fin.", ex.getMessage());
    }

    // =========================================================
    // TESTS PARA validarPresupuestoYLogistica()
    // =========================================================

    /**
     * T5 - CORRECTO
     * Presupuesto, meta y descripción con valores válidos.
     * No debe lanzar ninguna excepción.
     */
    @Test
    public void testPresupuestoYLogistica_correcto() {
        ini.setPresupuesto(1500.00);
        ini.setMeta(100);
        ini.setDescripcion("Limpieza del parque central del sector norte.");

        assertDoesNotThrow(() -> controlador.validarPresupuestoYLogistica(ini));
    }

    /**
     * T6 - ERROR
     * Presupuesto igual a 0.
     * Debe lanzar IllegalArgumentException con el mensaje correspondiente.
     */
    @Test
    public void testPresupuestoCero_error() {
        ini.setPresupuesto(0);
        ini.setMeta(100);
        ini.setDescripcion("Descripción válida.");

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> controlador.validarPresupuestoYLogistica(ini)
        );
        assertEquals("El presupuesto debe ser una cantidad mayor a 0.", ex.getMessage());
    }

    /**
     * T7 - ERROR
     * Meta de participantes igual a 0.
     * Debe lanzar IllegalArgumentException con el mensaje correspondiente.
     */
    @Test
    public void testMetaCero_error() {
        ini.setPresupuesto(1500.00);
        ini.setMeta(0);
        ini.setDescripcion("Descripción válida.");

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> controlador.validarPresupuestoYLogistica(ini)
        );
        assertEquals("Debe asignar una cantidad de participantes mayor a 0.", ex.getMessage());
    }

    /**
     * T8 - ERROR
     * Meta de participantes mayor o igual a 5000.
     * Debe lanzar IllegalArgumentException con el mensaje correspondiente.
     */
    @Test
    public void testMetaMayorIgual5000_error() {
        ini.setPresupuesto(1500.00);
        ini.setMeta(5000);
        ini.setDescripcion("Descripción válida.");

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> controlador.validarPresupuestoYLogistica(ini)
        );
        assertEquals("Debe asignar una cantidad de participantes menor a 5000.", ex.getMessage());
    }

    /**
     * T9 - ERROR
     * Descripción logística que supera los 500 caracteres.
     * Debe lanzar IllegalArgumentException con el mensaje correspondiente.
     */
    @Test
    public void testDescripcionMayor500Caracteres_error() {
        ini.setPresupuesto(1500.00);
        ini.setMeta(100);
        ini.setDescripcion("A".repeat(501));

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> controlador.validarPresupuestoYLogistica(ini)
        );
        assertEquals("La descripción logística excede el límite de 500 caracteres.", ex.getMessage());
    }
}