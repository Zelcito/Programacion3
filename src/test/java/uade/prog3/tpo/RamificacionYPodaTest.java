package uade.prog3.tpo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import uade.prog3.tpo.algorithm.RamificacionYPoda;
import uade.prog3.tpo.model.Item;
import uade.prog3.tpo.seed.CargaInicial;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** Reparto de las provisiones del Temple, sin Spring ni Neo4j. */
class RamificacionYPodaTest {

    private final RamificacionYPoda ramificacionYPoda = new RamificacionYPoda();

    @Test
    void dosMulasTienenCargaMaximaOptimaDe17() {
        verificarOptimo(2, 17);
    }

    @Test
    void tresMulasTienenCargaMaximaOptimaDe11() throws JsonProcessingException {
        RamificacionYPoda.Asignacion resultado = verificarOptimo(3, 11);
        // Respuesta reproducible de la ficha, con el orden de CargaInicial.
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(resultado));
    }

    @Test
    void cuatroMulasTienenCargaMaximaOptimaDe9() {
        verificarOptimo(4, 9);
    }

    private RamificacionYPoda.Asignacion verificarOptimo(int mulas, double esperado) {
        List<Item> items = CargaInicial.PROVISIONES;
        RamificacionYPoda.Asignacion resultado = ramificacionYPoda.repartir(items, mulas);
        Exhaustiva exhaustiva = new Exhaustiva();
        exhaustiva.asignar(items, 0, new double[mulas]);

        assertEquals(esperado, exhaustiva.mejorCarga);
        assertEquals(exhaustiva.mejorCarga, resultado.cargaMaxima());
        assertEsAsignacionCompleta(items, mulas, resultado);
        assertTrue(resultado.nodosPodados() > 0);
        assertTrue(resultado.nodosPodados() < resultado.nodosExplorados());
        assertTrue(resultado.nodosExplorados() < exhaustiva.nodosExplorados);
        System.out.printf("Mulas=%d, carga=%.0f, explorados=%d, podados=%d, sin poda=%d%n",
                mulas, resultado.cargaMaxima(), resultado.nodosExplorados(), resultado.nodosPodados(),
                exhaustiva.nodosExplorados);
        return resultado;
    }

    private void assertEsAsignacionCompleta(List<Item> items, int mulas, RamificacionYPoda.Asignacion resultado) {
        assertEquals(mulas, resultado.porContenedor().size());
        Map<Item, Integer> apariciones = new HashMap<>();
        double cargaMaxima = 0;
        for (int i = 1; i <= mulas; i++) {
            List<Item> carga = resultado.porContenedor().get("Mula " + i);
            assertNotNull(carga, "Mula " + i);
            double peso = 0;
            for (Item item : carga) {
                apariciones.merge(item, 1, Integer::sum);
                peso += item.getPeso();
            }
            cargaMaxima = Math.max(cargaMaxima, peso);
        }
        assertEquals(items.size(), apariciones.size());
        for (Item item : items) {
            assertEquals(1, apariciones.get(item), item.getId());
        }
        assertEquals(cargaMaxima, resultado.cargaMaxima());
    }

    /** Enumera cada asignacion y calcula el maximo solo en las hojas. */
    private static final class Exhaustiva {
        private double mejorCarga = Double.POSITIVE_INFINITY;
        private long nodosExplorados;

        void asignar(List<Item> items, int indice, double[] cargas) {
            nodosExplorados++; // mismo criterio: una unidad por llamada, incluida la raiz
            if (indice == items.size()) {
                double maximo = 0;
                for (double carga : cargas) {
                    maximo = Math.max(maximo, carga);
                }
                mejorCarga = Math.min(mejorCarga, maximo);
                return;
            }
            for (int mula = 0; mula < cargas.length; mula++) {
                double anterior = cargas[mula];
                cargas[mula] += items.get(indice).getPeso();
                asignar(items, indice + 1, cargas);
                cargas[mula] = anterior;
            }
        }
    }

    @Test
    void unaMulaLlevaLos33Kg() {
        List<Item> items = CargaInicial.PROVISIONES;
        RamificacionYPoda.Asignacion resultado = ramificacionYPoda.repartir(items, 1);
        assertEquals(33.0, resultado.cargaMaxima());
        assertEsAsignacionCompleta(items, 1, resultado);
        assertEquals(items.size() + 1L, resultado.nodosExplorados());
        assertEquals(0, resultado.nodosPodados());
    }

    @Test
    void sinProvisionesTodasLasMulasQuedanVacias() {
        RamificacionYPoda.Asignacion resultado = ramificacionYPoda.repartir(List.of(), 3);
        assertEsAsignacionCompleta(List.of(), 3, resultado);
        assertEquals(1, resultado.nodosExplorados());
        assertEquals(0, resultado.nodosPodados());
    }

    @Test
    void mulasSobrantesQuedanVaciasYLasLlamadasPodadasTambienSeCuentan() {
        List<Item> items = List.of(CargaInicial.PROVISIONES.get(0));
        RamificacionYPoda.Asignacion resultado = ramificacionYPoda.repartir(items, 3);
        assertEsAsignacionCompleta(items, 3, resultado);
        assertEquals(2.0, resultado.cargaMaxima());
        // Raiz y tres decisiones. La primera hoja fija el optimo y las otras empatan.
        assertEquals(4, resultado.nodosExplorados());
        assertEquals(2, resultado.nodosPodados());
    }

    @Test
    void admitePesosDecimalesYCero() {
        Item a = new Item("A", "A", 0.3, 0, null);
        Item b = new Item("B", "B", 0.2, 0, null);
        Item c = new Item("C", "C", 0.1, 0, null);
        Item d = new Item("D", "D", 0, 0, null);
        RamificacionYPoda.Asignacion resultado = ramificacionYPoda.repartir(List.of(a, b, c, d), 2);
        assertEquals(0.3, resultado.cargaMaxima());
        assertEquals(List.of(a, d), resultado.porContenedor().get("Mula 1"));
        assertEquals(List.of(b, c), resultado.porContenedor().get("Mula 2"));
    }

    @Test
    void rechazaCantidadInvalidaYPesosNegativosONoFinitos() {
        assertThrows(IllegalArgumentException.class, () -> ramificacionYPoda.repartir(List.of(), 0));
        assertThrows(IllegalArgumentException.class, () -> ramificacionYPoda.repartir(List.of(), -1));
        for (double peso : new double[]{-1, Double.NaN, Double.POSITIVE_INFINITY}) {
            Item invalido = new Item("X", "Invalido", peso, 0, null);
            assertThrows(IllegalArgumentException.class, () -> ramificacionYPoda.repartir(List.of(invalido), 2));
        }
    }
}
