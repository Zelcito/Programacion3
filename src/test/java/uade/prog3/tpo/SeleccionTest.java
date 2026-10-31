package uade.prog3.tpo;

import org.junit.jupiter.api.Test;
import uade.prog3.tpo.algorithm.Seleccion;
import uade.prog3.tpo.model.Item;
import uade.prog3.tpo.seed.CargaInicial;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de la mochila 0/1 con greedy y con programacion dinamica, sobre las
 * 9 provisiones de La Ruta del Temple (CargaInicial.PROVISIONES). No tocan Neo4j.
 *
 * Valores calculados a mano (ver las fichas de greedy y dinamica en docs/ENDPOINTS.md):
 * con 15 kg greedy junta 19 jornadas y el optimo es 22.
 */
class SeleccionTest {

    private final Seleccion seleccion = new Seleccion();

    private Set<String> ids(Seleccion.Resultado r) {
        Set<String> ids = new HashSet<>();
        for (Item item : r.elegidos()) {
            ids.add(item.getId());
        }
        return ids;
    }

    @Test
    void greedyConQuinceKgJunta19JornadasEn13Kg() {
        Seleccion.Resultado r = seleccion.greedy(CargaInicial.PROVISIONES, 15);
        // ratio 2, 2, 1.25, 1, 1: le quedan 2 kg libres que ya no puede usar
        assertEquals(Set.of("CAR", "ODR", "QUE", "PAN", "SAL"), ids(r));
        assertEquals(19.0, r.valorTotal());
        assertEquals(13.0, r.pesoTotal());
        assertEquals("GREEDY", r.tecnica());
    }

    @Test
    void dinamicaConQuinceKgJunta22JornadasEn15Kg() {
        Seleccion.Resultado r = seleccion.programacionDinamica(CargaInicial.PROVISIONES, 15);
        assertEquals(22.0, r.valorTotal());
        assertEquals(15.0, r.pesoTotal());
        // PAN y SAL son iguales (2 kg, 2 jornadas): hay dos selecciones optimas
        assertTrue(ids(r).equals(Set.of("PAN", "CAR", "ODR", "VIN"))
                || ids(r).equals(Set.of("CAR", "ODR", "VIN", "SAL")), "seleccion: " + ids(r));
        assertEquals("PROGRAMACION_DINAMICA", r.tecnica());
    }

    @Test
    void greedyPierdeTresJornadasContraElOptimo() {
        double greedy = seleccion.greedy(CargaInicial.PROVISIONES, 15).valorTotal();
        double optimo = seleccion.programacionDinamica(CargaInicial.PROVISIONES, 15).valorTotal();
        assertEquals(3.0, optimo - greedy);
    }

    @Test
    void greedyNoDependeDelOrdenDeEntrada() {
        // Neo4j no garantiza el orden: el desempate por peso hace que QUE vaya antes que VIN
        List<Item> invertidas = new ArrayList<>();
        for (int i = CargaInicial.PROVISIONES.size() - 1; i >= 0; i--) {
            invertidas.add(CargaInicial.PROVISIONES.get(i));
        }
        Seleccion.Resultado r = seleccion.greedy(invertidas, 15);
        assertEquals(19.0, r.valorTotal());
        assertEquals(Set.of("CAR", "ODR", "QUE", "PAN", "SAL"), ids(r));
    }

    @Test
    void dinamicaCoincideConLaFuerzaBrutaParaTodasLasCapacidades() {
        List<Item> items = CargaInicial.PROVISIONES;
        int n = items.size();
        for (int capacidad = 0; capacidad <= 33; capacidad++) {
            // fuerza bruta: los 2^9 = 512 subconjuntos
            double mejor = 0;
            for (int mascara = 0; mascara < (1 << n); mascara++) {
                double peso = 0;
                double valor = 0;
                for (int i = 0; i < n; i++) {
                    if ((mascara & (1 << i)) != 0) {
                        peso += items.get(i).getPeso();
                        valor += items.get(i).getValor();
                    }
                }
                if (peso <= capacidad && valor > mejor) {
                    mejor = valor;
                }
            }
            Seleccion.Resultado r = seleccion.programacionDinamica(items, capacidad);
            assertEquals(mejor, r.valorTotal(), "capacidad " + capacidad);
            assertTrue(r.pesoTotal() <= capacidad, "capacidad " + capacidad);

            // los elegidos tienen que sumar lo que dice el resultado
            double suma = 0;
            for (Item item : r.elegidos()) {
                suma += item.getValor();
            }
            assertEquals(r.valorTotal(), suma, "capacidad " + capacidad);

            // greedy nunca supera al optimo
            assertTrue(seleccion.greedy(items, capacidad).valorTotal() <= mejor, "capacidad " + capacidad);
        }
    }

    @Test
    void capacidadCeroNoEligeNada() {
        Seleccion.Resultado greedy = seleccion.greedy(CargaInicial.PROVISIONES, 0);
        Seleccion.Resultado dinamica = seleccion.programacionDinamica(CargaInicial.PROVISIONES, 0);
        assertTrue(greedy.elegidos().isEmpty());
        assertEquals(0.0, greedy.valorTotal());
        assertTrue(dinamica.elegidos().isEmpty());
        assertEquals(0.0, dinamica.valorTotal());
    }

    @Test
    void capacidadNegativaSeRechaza() {
        assertThrows(IllegalArgumentException.class,
                () -> seleccion.greedy(CargaInicial.PROVISIONES, -1));
        assertThrows(IllegalArgumentException.class,
                () -> seleccion.programacionDinamica(CargaInicial.PROVISIONES, -1));
    }

    @Test
    void dinamicaRechazaPesosNoEnteros() {
        List<Item> items = List.of(new Item("X", "Media hogaza", 1.5, 1, null));
        assertThrows(IllegalArgumentException.class, () -> seleccion.programacionDinamica(items, 5));
    }
}
