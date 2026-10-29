package uade.prog3.tpo.algorithm;

import org.springframework.stereotype.Component;
import uade.prog3.tpo.service.Grafo;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

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
     *
     * Implementacion: heap binario (PriorityQueue) con insercion perezosa. En vez
     * de bajar la prioridad de un vertice que ya esta en la cola, se encola de
     * nuevo con la distancia mejor y la entrada vieja se descarta al sacarla.
     * Cada relajacion exitosa encola una vez: a lo sumo una entrada por arista,
     * cada una O(log E) = O(log V) para entrar y salir. Total O((V + E) log V).
     */
    public Camino dijkstra(Grafo grafo, String origenId, String destinoId) {
        int origen = grafo.indiceDe(origenId);
        int destino = grafo.indiceDe(destinoId);
        validarSinCostosNegativos(grafo);

        int n = grafo.cantidadVertices();
        double[] dist = new double[n];
        int[] predecesor = new int[n];
        for (int v = 0; v < n; v++) {
            dist[v] = Grafo.INFINITO;
            predecesor[v] = -1;
        }
        dist[origen] = 0;

        PriorityQueue<EnCola> cola = new PriorityQueue<>(Comparator.comparingDouble(EnCola::distancia));
        cola.add(new EnCola(origen, 0));

        while (!cola.isEmpty()) {
            EnCola actual = cola.poll();
            int u = actual.vertice();
            if (actual.distancia() > dist[u]) {
                continue; // entrada vieja: u ya salio antes con una distancia menor
            }
            // greedy: u es el no fijado mas cercano y con costos >= 0 nadie lo puede mejorar
            for (Grafo.Arista a : grafo.vecinos(u)) {
                int v = a.destino();
                if (dist[u] + a.costo() < dist[v]) { // RELAJACION
                    dist[v] = dist[u] + a.costo();
                    predecesor[v] = u;
                    cola.add(new EnCola(v, dist[v]));
                }
            }
        }

        if (dist[destino] == Grafo.INFINITO) {
            throw new IllegalStateException("No hay ningun camino de " + origenId + " a " + destinoId);
        }
        return new Camino(reconstruir(grafo, predecesor, destino), dist[destino]);
    }

    /** Vertice en la cola de prioridad con la distancia que tenia al encolarlo. */
    private record EnCola(int vertice, double distancia) { }

    /** Con una sola arista negativa el greedy de Dijkstra puede fijar una distancia equivocada. */
    private void validarSinCostosNegativos(Grafo grafo) {
        for (int u = 0; u < grafo.cantidadVertices(); u++) {
            for (Grafo.Arista a : grafo.vecinos(u)) {
                if (a.costo() < 0) {
                    throw new IllegalArgumentException("Dijkstra no admite costos negativos: la ruta "
                            + grafo.idDe(u) + "-" + grafo.idDe(a.destino()) + " cuesta " + a.costo());
                }
            }
        }
    }

    /** Sigue los predecesores desde el destino hasta el origen. Push al frente deja el origen primero. */
    private List<String> reconstruir(Grafo grafo, int[] predecesor, int destino) {
        Deque<String> camino = new ArrayDeque<>();
        for (int v = destino; v != -1; v = predecesor[v]) {
            camino.push(grafo.idDe(v));
        }
        return new ArrayList<>(camino);
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
