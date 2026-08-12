package uade.prog3.tpo;

import org.junit.jupiter.api.Test;
import uade.prog3.tpo.service.Grafo;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de la estructura de grafo en memoria.
 *
 * NO tocan Neo4j: son unitarios y corren en milisegundos.
 * Cada grupo debe agregar aca los tests de SUS algoritmos.
 * Un algoritmo sin al menos un test no se considera implementado.
 */
class GrafoTest {

    /** Grafo chico de referencia, el mismo que usa la carga inicial. */
    private Grafo grafoDeEjemplo(boolean dirigido) {
        Grafo g = new Grafo(dirigido);
        g.agregarArista("A", "B", 4);
        g.agregarArista("A", "C", 9);
        g.agregarArista("B", "C", 3);
        g.agregarArista("C", "D", 2);
        return g;
    }

    @Test
    void construyeVerticesYAristas() {
        Grafo g = grafoDeEjemplo(true);
        assertEquals(4, g.cantidadVertices());
        assertEquals(4, g.cantidadAristas());
        assertTrue(g.esDirigido());
    }

    @Test
    void noDirigidoDuplicaLaArista() {
        Grafo g = grafoDeEjemplo(false);
        assertEquals(4, g.cantidadAristas(), "las aristas no se cuentan dos veces");
        assertEquals(2, g.vecinos(g.indiceDe("A")).size());
        assertEquals(2, g.vecinos(g.indiceDe("B")).size(), "B debe ver a A en no dirigido");
    }

    @Test
    void matrizDeAdyacenciaRespetaInfinitoYDiagonal() {
        Grafo g = grafoDeEjemplo(true);
        double[][] m = g.matrizDeAdyacencia();
        int a = g.indiceDe("A");
        int d = g.indiceDe("D");
        assertEquals(0.0, m[a][a]);
        assertEquals(Grafo.INFINITO, m[a][d], "no hay arista directa A->D");
        assertEquals(4.0, m[a][g.indiceDe("B")]);
    }

    @Test
    void idDesconocidoFalla() {
        Grafo g = grafoDeEjemplo(true);
        assertThrows(IllegalArgumentException.class, () -> g.indiceDe("Z"));
    }
}
