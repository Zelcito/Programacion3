package uade.prog3.tpo;

import org.junit.jupiter.api.Test;
import uade.prog3.tpo.algorithm.Recorridos;
import uade.prog3.tpo.seed.CargaInicial;
import uade.prog3.tpo.service.Grafo;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de BFS y DFS sobre La Ruta del Temple. No tocan Neo4j: arman el grafo
 * con los mismos datos que carga CargaInicial, como lo hace GrafoService
 * (primero los vertices, despues las aristas, no dirigido).
 *
 * Los ordenes esperados estan calculados a mano recorriendo la lista de
 * adyacencia en el orden de la tabla de rutas del README.
 */
class RecorridosTest {

    private final Recorridos recorridos = new Recorridos();

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

    @Test
    void elDominioTiene20CiudadesY36Rutas() {
        Grafo g = rutaDelTemple();
        assertEquals(20, g.cantidadVertices());
        assertEquals(36, g.cantidadAristas());
    }

    @Test
    void bfsDesdeParisRecorrePorNiveles() {
        // nivel 0: PAR · 1: TRO LYO · 2: MRS GEN · 3: BCN ROM VEN · 4: PAL BRI
        // 5: CRE TES ATE · 6: ROD CHI CON · 7: ICO TRI ANT JER
        assertEquals(List.of(
                "PAR", "TRO", "LYO", "MRS", "GEN", "BCN", "ROM", "VEN", "PAL", "BRI",
                "CRE", "TES", "ATE", "ROD", "CHI", "CON", "ICO", "TRI", "ANT", "JER"),
                recorridos.bfs(rutaDelTemple(), "PAR"));
    }

    @Test
    void dfsDesdeParisBajaHastaElFondoAntesDeRetroceder() {
        assertEquals(List.of(
                "PAR", "TRO", "LYO", "MRS", "BCN", "PAL", "ROM", "GEN", "VEN", "BRI",
                "TES", "ATE", "CRE", "ROD", "CON", "ICO", "ANT", "CHI", "TRI", "JER"),
                recorridos.dfs(rutaDelTemple(), "PAR"));
    }

    @Test
    void ambosVisitanCadaCiudadUnaSolaVez() {
        Grafo g = rutaDelTemple();
        for (String origen : g.ids()) {
            List<String> porBfs = recorridos.bfs(g, origen);
            List<String> porDfs = recorridos.dfs(g, origen);
            assertEquals(20, porBfs.size(), "BFS desde " + origen);
            assertEquals(20, new HashSet<>(porBfs).size(), "BFS repite ciudades desde " + origen);
            assertEquals(20, porDfs.size(), "DFS desde " + origen);
            assertEquals(20, new HashSet<>(porDfs).size(), "DFS repite ciudades desde " + origen);
            assertEquals(origen, porBfs.get(0));
            assertEquals(origen, porDfs.get(0));
        }
    }

    @Test
    void enUnGrafoDirigidoSoloSigueElSentidoDeLasAristas() {
        Grafo g = new Grafo(true);
        g.agregarArista("A", "B", 1);
        g.agregarArista("B", "C", 1);
        g.agregarArista("D", "A", 1);
        assertEquals(List.of("A", "B", "C"), recorridos.bfs(g, "A"));
        assertEquals(List.of("A", "B", "C"), recorridos.dfs(g, "A"));
    }

    @Test
    void verticeAisladoSoloSeVisitaASiMismo() {
        Grafo g = rutaDelTemple();
        g.agregarVertice("ISL");
        assertEquals(List.of("ISL"), recorridos.bfs(g, "ISL"));
        assertEquals(List.of("ISL"), recorridos.dfs(g, "ISL"));
    }

    @Test
    void origenInexistenteFalla() {
        Grafo g = rutaDelTemple();
        assertThrows(IllegalArgumentException.class, () -> recorridos.bfs(g, "XXX"));
        assertThrows(IllegalArgumentException.class, () -> recorridos.dfs(g, "XXX"));
    }
}
