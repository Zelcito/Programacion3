package uade.prog3.tpo;

import org.junit.jupiter.api.Test;
import uade.prog3.tpo.algorithm.CaminosMinimos;
import uade.prog3.tpo.seed.CargaInicial;
import uade.prog3.tpo.service.Grafo;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** Tests sobre los costos base del dominio, sin Spring ni Neo4j. */
class FloydWarshallTest {

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

    @Test
    void caminosConocidosDelDominio() {
        Map<String, Double> dist = caminosMinimos.floydWarshall(rutaDelTemple());
        // PAR->LYO->MRS->ROM: 3+2+4; ROM->BRI->ATE->ROD->CHI->JER: 4+4+3+3+3
        assertEquals(9.0, dist.get("PAR->ROM"));
        assertEquals(17.0, dist.get("ROM->JER"));
        assertEquals(26.0, dist.get("PAR->JER"));
    }

    @Test
    void cuatrocientosParesConDiagonalCeroSimetriaYDiametro26() {
        Grafo g = rutaDelTemple();
        Map<String, Double> dist = caminosMinimos.floydWarshall(g);
        assertEquals(400, dist.size());
        double diametro = 0;
        for (String origen : g.ids()) {
            assertEquals(0.0, dist.get(origen + "->" + origen));
            for (String destino : g.ids()) {
                double costo = dist.get(origen + "->" + destino);
                assertEquals(costo, dist.get(destino + "->" + origen));
                diametro = Math.max(diametro, costo);
            }
        }
        assertEquals(26.0, diametro);
    }

    @Test
    void coincideConElDijkstraPublicoParaTodosLosPares() {
        Grafo g = rutaDelTemple();
        Map<String, Double> dist = caminosMinimos.floydWarshall(g);
        for (String origen : g.ids()) {
            for (String destino : g.ids()) {
                assertEquals(caminosMinimos.dijkstra(g, origen, destino).costoTotal(),
                        dist.get(origen + "->" + destino), origen + "->" + destino);
            }
        }
    }

    @Test
    void comparaUnaEjecucionDeFloydContraVeinteDeDijkstra() {
        CaminosMinimos.Comparacion resultado = caminosMinimos.compararConDijkstra(rutaDelTemple());
        assertEquals(resultado.floyd().distancias(), resultado.distanciasDijkstra());
        assertEquals(20, resultado.ejecucionesDijkstra());
        assertEquals(8000, resultado.floyd().iteraciones());
        assertEquals(3901, resultado.floyd().intentos());
        assertEquals(468, resultado.floyd().mejoras());
        assertEquals(437, resultado.dijkstra().extracciones());
        assertEquals(417, resultado.dijkstra().mejoras());
        assertEquals(400, resultado.dijkstra().nodosExpandidos());
        assertEquals(1440, resultado.dijkstra().intentos()); // 20 origenes * 36 rutas * 2 sentidos
        assertEquals(resultado.dijkstra().mejoras() + 20, resultado.dijkstra().extracciones());
        System.out.println("Comparacion Temple: Floyd iteraciones=" + resultado.floyd().iteraciones()
                + ", intentos=" + resultado.floyd().intentos() + ", mejoras=" + resultado.floyd().mejoras()
                + "; Dijkstra=" + resultado.dijkstra());
    }

    @Test
    void metricasDistinguenEntradasViejasDeExpansiones() {
        Grafo g = new Grafo(true);
        g.agregarArista("A", "B", 5);
        g.agregarArista("A", "C", 1);
        g.agregarArista("C", "B", 1);
        CaminosMinimos.Comparacion resultado = caminosMinimos.compararConDijkstra(g);
        // Desde A: 4 extracciones, 3 expansiones, 3 intentos y 3 mejoras.
        // Desde B: 1,1,0,0. Desde C: 2,2,1,1. La entrada vieja de B no se expande.
        assertEquals(new CaminosMinimos.MetricasDijkstra(7, 6, 4, 4), resultado.dijkstra());
        assertEquals(27, resultado.floyd().iteraciones());
        assertEquals(10, resultado.floyd().intentos());
        assertEquals(1, resultado.floyd().mejoras());
    }

    @Test
    void respetaElOrdenDeIntermediosAunqueLosIdsNoSiganElCamino() {
        Grafo g = new Grafo(true);
        for (String id : new String[]{"A", "B", "C", "D"}) {
            g.agregarVertice(id);
        }
        g.agregarArista("A", "C", 1);
        g.agregarArista("C", "D", 1);
        g.agregarArista("D", "B", 1);
        assertEquals(3.0, caminosMinimos.floydWarshall(g).get("A->B"));
    }

    @Test
    void aceptaAristasNegativasSinCicloYOmiteLosInalcanzables() {
        Grafo g = new Grafo(true);
        g.agregarArista("A", "B", 4);
        g.agregarArista("B", "C", -2);
        g.agregarArista("A", "C", 8);
        Map<String, Double> dist = caminosMinimos.floydWarshall(g);
        assertEquals(2.0, dist.get("A->C"));
        assertFalse(dist.containsKey("C->A"));
        assertEquals(6, dist.size());
        assertThrows(IllegalArgumentException.class, () -> caminosMinimos.compararConDijkstra(g));
    }

    @Test
    void detectaCicloNegativoInclusoEnOtraComponente() {
        Grafo g = new Grafo(true);
        g.agregarVertice("ISL");
        g.agregarArista("A", "B", 1);
        g.agregarArista("B", "C", -3);
        g.agregarArista("C", "A", 1);
        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> caminosMinimos.floydWarshall(g));
        assertTrue(error.getMessage().contains("ciclo de costo negativo"));
    }

    @Test
    void detectaLazoNegativoYAristaNegativaNoDirigida() {
        Grafo lazo = new Grafo(true);
        lazo.agregarArista("A", "A", -1);
        assertThrows(IllegalStateException.class, () -> caminosMinimos.floydWarshall(lazo));
        Grafo g = new Grafo(false);
        g.agregarArista("A", "B", -1);
        assertThrows(IllegalStateException.class, () -> caminosMinimos.floydWarshall(g));
    }

    @Test
    void grafoVacioYVerticesAislados() {
        Grafo g = new Grafo(false);
        assertTrue(caminosMinimos.floydWarshall(g).isEmpty());
        assertEquals(0, caminosMinimos.compararConDijkstra(g).ejecucionesDijkstra());
        g.agregarVertice("A");
        g.agregarVertice("B");
        assertEquals(Map.of("A->A", 0.0, "B->B", 0.0), caminosMinimos.floydWarshall(g));
        assertEquals(caminosMinimos.floydWarshall(g),
                caminosMinimos.compararConDijkstra(g).distanciasDijkstra());
    }

    @Test
    void conservaElGrafoYUsaLaMenorAristaParalela() {
        Grafo g = new Grafo(true);
        g.agregarArista("A", "B", 2);
        g.agregarArista("A", "B", 7);
        g.agregarArista("B", "C", 0);
        double[][] antes = g.matrizDeAdyacencia();
        assertEquals(2.0, caminosMinimos.floydWarshall(g).get("A->C"));
        double[][] despues = g.matrizDeAdyacencia();
        for (int i = 0; i < antes.length; i++) {
            assertArrayEquals(antes[i], despues[i]);
        }
    }
}
