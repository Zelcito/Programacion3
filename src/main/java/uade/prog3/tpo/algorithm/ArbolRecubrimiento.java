package uade.prog3.tpo.algorithm;

import org.springframework.stereotype.Component;
import uade.prog3.tpo.service.Grafo;

import java.util.List;

/**
 * UNIDAD: Grafos II (clase 5)
 * PUNTAJE: forma parte de los 3 puntos de "Dijkstra, Prim, Kruskal"
 *
 * ATENCION: el arbol de recubrimiento minimo solo tiene sentido sobre un
 * grafo NO DIRIGIDO y CONEXO. Al cargar el grafo hay que pedirlo con
 * grafoService.cargar(false).
 */
@Component
public class ArbolRecubrimiento {

    /** Una arista del arbol resultante, con ids de negocio. */
    public record AristaMst(String origen, String destino, double costo) { }

    /** Arbol de recubrimiento minimo completo. */
    public record Mst(List<AristaMst> aristas, double costoTotal) { }

    /**
     * Prim: hace crecer un unico arbol desde un vertice inicial, tomando en
     * cada paso la arista de menor costo que cruza de dentro hacia afuera.
     *
     * Requisitos:
     *   - arreglos key[], parent[] e inMST[], o bien una cola de prioridad
     *   - el resultado debe tener exactamente V-1 aristas si el grafo es conexo
     *   - si el grafo NO es conexo, fallar con un mensaje claro
     *
     * Complejidad esperada: O(V^2) con busqueda lineal, O(E log V) con heap.
     * Hay que decir cual de las dos implementaron y por que.
     */
    public Mst prim(Grafo grafo, String origenId) {
        throw new PendienteDeImplementar("Prim");
    }

    /**
     * Kruskal: ordena TODAS las aristas por costo y las agrega salteando las
     * que formarian ciclo.
     *
     * Requisitos:
     *   - implementar Union-Find (find y union) a mano, sin librerias
     *   - la deteccion de ciclo es: find(u) == find(v)
     *   - terminar al llegar a V-1 aristas
     *
     * Complejidad esperada: O(E log E).
     *
     * Para el coloquio: saber justificar en que caso conviene Prim y en cual
     * Kruskal, en funcion de la densidad del grafo.
     */
    public Mst kruskal(Grafo grafo) {
        throw new PendienteDeImplementar("Kruskal");
    }
}
