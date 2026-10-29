package uade.prog3.tpo;

import org.junit.jupiter.api.Test;
import uade.prog3.tpo.algorithm.CaminosMinimos;
import uade.prog3.tpo.seed.CargaInicial;
import uade.prog3.tpo.service.Grafo;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de Dijkstra sobre La Ruta del Temple y sobre el grafo A..H de GUIA-TPO.md.
 * No tocan Neo4j: arman el grafo con los datos de CargaInicial, como RecorridosTest.
 *
 * Los costos esperados estan calculados a mano (ver la ficha de dijkstra en
 * docs/ENDPOINTS.md). Todos los caminos minimos del dominio que se prueban son unicos.
 */
class CaminosMinimosTest {

    private final CaminosMinimos caminosMinimos = new CaminosMinimos();

    private Grafo rutaDelTemple() {
        Grafo g = new Grafo(false);
        for (CargaInicial.Ciudad c : CargaInicial.CIUDADES) {
            g.agregarVertice(c.id());
        }
        for (CargaInicial.Ruta r : CargaInicial.RUTAS) {
            g.agregarArista(r.origen(), r.destino(), r.jornadas());
        }
        return g;
    }

    /** Grafo de ejemplo del scaffold (GUIA-TPO.md): A->C = 7, A->H = 20. */
    private Grafo grafoDeEjemplo() {
        Grafo g = new Grafo(false);
        g.agregarArista("A", "B", 4);
        g.agregarArista("A", "C", 9);
        g.agregarArista("B", "C", 3);
        g.agregarArista("B", "D", 7);
        g.agregarArista("C", "D", 2);
        g.agregarArista("C", "E", 11);
        g.agregarArista("D", "E", 5);
        g.agregarArista("D", "F", 8);
        g.agregarArista("E", "F", 6);
        g.agregarArista("E", "G", 10);
        g.agregarArista("F", "H", 3);
        g.agregarArista("G", "H", 4);
        return g;
    }

    @Test
    void dijkstraDeParisAJerusalenCuesta26() {
        CaminosMinimos.Camino camino = caminosMinimos.dijkstra(rutaDelTemple(), "PAR", "JER");
        assertEquals(26.0, camino.costoTotal());
        // 3 + 2 + 4 + 4 + 4 + 3 + 3 + 3: ocho etapas, una mas que BFS (7 etapas, 29 jornadas)
        assertEquals(List.of("PAR", "LYO", "MRS", "ROM", "BRI", "ATE", "ROD", "CHI", "JER"),
                camino.vertices());
    }

    @Test
    void dijkstraDesdeParisAOtrasCiudades() {
        Grafo g = rutaDelTemple();
        assertEquals(9.0, caminosMinimos.dijkstra(g, "PAR", "ROM").costoTotal());
        assertEquals(17.0, caminosMinimos.dijkstra(g, "PAR", "ATE").costoTotal());
        assertEquals(21.0, caminosMinimos.dijkstra(g, "PAR", "CON").costoTotal());
        assertEquals(23.0, caminosMinimos.dijkstra(g, "PAR", "CHI").costoTotal());
        assertEquals(17.0, caminosMinimos.dijkstra(g, "ROM", "JER").costoTotal());
    }

    @Test
    void dijkstraPrefiereElDesvioMasBaratoAlCaminoDirecto() {
        // A->C directo cuesta 9; A->B->C cuesta 4 + 3 = 7
        CaminosMinimos.Camino camino = caminosMinimos.dijkstra(grafoDeEjemplo(), "A", "C");
        assertEquals(7.0, camino.costoTotal());
        assertEquals(List.of("A", "B", "C"), camino.vertices());
    }

    @Test
    void dijkstraDeAaHEnElGrafoDeEjemploCuesta20() {
        CaminosMinimos.Camino camino = caminosMinimos.dijkstra(grafoDeEjemplo(), "A", "H");
        assertEquals(20.0, camino.costoTotal());
        assertEquals(List.of("A", "B", "C", "D", "F", "H"), camino.vertices());
    }

    @Test
    void origenIgualADestinoEsUnCaminoDeCostoCero() {
        CaminosMinimos.Camino camino = caminosMinimos.dijkstra(rutaDelTemple(), "PAR", "PAR");
        assertEquals(List.of("PAR"), camino.vertices());
        assertEquals(0.0, camino.costoTotal());
    }

    @Test
    void ciudadInexistenteFalla() {
        Grafo g = rutaDelTemple();
        assertThrows(IllegalArgumentException.class, () -> caminosMinimos.dijkstra(g, "XXX", "JER"));
        assertThrows(IllegalArgumentException.class, () -> caminosMinimos.dijkstra(g, "PAR", "XXX"));
    }

    @Test
    void rutaDeCostoNegativoSeRechaza() {
        Grafo g = rutaDelTemple();
        g.agregarArista("ROM", "JER", -1);
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> caminosMinimos.dijkstra(g, "PAR", "JER"));
        assertTrue(error.getMessage().contains("negativ"), error.getMessage());
    }

    @Test
    void destinoInalcanzableFallaConErrorManejado() {
        Grafo g = rutaDelTemple();
        g.agregarVertice("ISL");
        assertThrows(IllegalStateException.class, () -> caminosMinimos.dijkstra(g, "PAR", "ISL"));
    }
}
