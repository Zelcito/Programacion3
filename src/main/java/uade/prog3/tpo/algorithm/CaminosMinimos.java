package uade.prog3.tpo.algorithm;

import org.springframework.stereotype.Component;
import uade.prog3.tpo.service.Grafo;

import java.util.List;
import java.util.Map;

/**
 * UNIDAD: Grafos II (clase 5) y PD sobre grafos (clase 8)
 * PUNTAJE: forma parte de los 3 puntos de "Dijkstra, Prim, Kruskal"
 *          y de 1 punto de programacion dinamica si implementan Floyd.
 */
@Component
public class CaminosMinimos {

    /** Resultado de una consulta de camino minimo. */
    public record Camino(List<String> vertices, double costoTotal) { }

    /**
     * Dijkstra: camino de costo minimo desde un origen hasta un destino.
     *
     * Requisitos:
     *   - usar cola de prioridad (PriorityQueue), no busqueda lineal del minimo
     *   - implementar la relajacion de aristas
     *   - mantener un arreglo de predecesores para poder RECONSTRUIR el camino,
     *     no solo devolver la distancia
     *   - validar que no haya aristas de costo negativo y fallar con un mensaje
     *     claro si las hay
     *
     * Complejidad esperada: O((V + E) log V) con heap binario.
     *
     * Para el coloquio: saber explicar por que Dijkstra es un algoritmo greedy
     * y por que los pesos negativos lo rompen.
     */
    public Camino dijkstra(Grafo grafo, String origenId, String destinoId) {
        throw new PendienteDeImplementar("Dijkstra");
    }

    /**
     * Floyd-Warshall: costo minimo entre TODOS los pares de vertices.
     *
     * Requisitos:
     *   - trabajar sobre la matriz de adyacencia (grafo.matrizDeAdyacencia())
     *   - el bucle del vertice intermedio k va SIEMPRE por fuera de los de i y j
     *   - detectar ciclos de costo negativo revisando la diagonal al terminar
     *
     * Complejidad esperada: O(V^3) en tiempo, O(V^2) en espacio.
     *
     * Devuelve un mapa "origen->destino" con el costo minimo de cada par.
     */
    public Map<String, Double> floydWarshall(Grafo grafo) {
        throw new PendienteDeImplementar("Floyd-Warshall");
    }

    /**
     * UCS (Uniform Cost Search): igual que Dijkstra pero con parada temprana
     * al expandir el destino.
     *
     * OPCIONAL. Solo suma si ya estan implementados Dijkstra y Floyd.
     * Si lo hacen, en el coloquio hay que poder mostrar la diferencia de
     * nodos expandidos respecto de Dijkstra sobre la misma consulta.
     */
    public Camino ucs(Grafo grafo, String origenId, String destinoId) {
        throw new PendienteDeImplementar("UCS");
    }
}
