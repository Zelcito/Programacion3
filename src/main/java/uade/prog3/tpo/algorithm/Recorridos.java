package uade.prog3.tpo.algorithm;

import org.springframework.stereotype.Component;
import uade.prog3.tpo.service.Grafo;

import java.util.List;

/**
 * UNIDAD: Grafos I (clase 4)          PUNTAJE: 2 puntos
 *
 * Recorridos sobre grafos. Es lo primero que conviene implementar:
 * es lo mas simple, vale 2 puntos, y ademas DFS es la base estructural
 * del backtracking que van a necesitar mas adelante.
 */
@Component
public class Recorridos {

    /**
     * Recorrido en profundidad desde un vertice.
     *
     * Requisitos:
     *   - implementacion recursiva o con pila explicita (no usar librerias)
     *   - marcar visitados para no entrar en ciclos infinitos
     *   - devolver los ids de los vertices EN EL ORDEN EN QUE SE VISITAN
     *
     * Complejidad esperada: O(V + E) con lista de adyacencia.
     *
     * @param grafo grafo cargado en memoria
     * @param origenId id del vertice de partida
     * @return lista de ids en orden de visita
     */
    public List<String> dfs(Grafo grafo, String origenId) {
        throw new PendienteDeImplementar("DFS");
    }

    /**
     * Recorrido en anchura desde un vertice.
     *
     * Requisitos:
     *   - usar una cola FIFO
     *   - marcar como visitado AL ENCOLAR, no al desencolar
     *   - devolver los ids en orden de visita
     *
     * Complejidad esperada: O(V + E).
     *
     * Para el coloquio: saber explicar por que BFS da el camino minimo
     * en cantidad de aristas pero NO en costo cuando el grafo es ponderado.
     */
    public List<String> bfs(Grafo grafo, String origenId) {
        throw new PendienteDeImplementar("BFS");
    }
}
