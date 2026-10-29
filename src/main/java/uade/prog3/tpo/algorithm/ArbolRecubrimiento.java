package uade.prog3.tpo.algorithm;

import org.springframework.stereotype.Component;
import uade.prog3.tpo.service.Grafo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

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

    /** Kruskal ordena las aristas con el MergeSort propio del hito 2. */
    private final Ordenamiento ordenamiento = new Ordenamiento();

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
     *
     * Implementada con heap (PriorityQueue): O(E log V). El grafo del dominio es
     * ralo (E = 36, V = 20, lejos de V^2), y ahi el heap le gana a recorrer los V
     * vertices en cada paso. Misma insercion perezosa que Dijkstra: si clave[v]
     * baja, v se encola de nuevo y la entrada vieja se descarta al salir.
     */
    public Mst prim(Grafo grafo, String origenId) {
        validarNoDirigido(grafo);
        int origen = grafo.indiceDe(origenId);
        int n = grafo.cantidadVertices();

        double[] clave = new double[n]; // costo de la arista mas barata que une v con el arbol
        int[] padre = new int[n];       // del otro lado de esa arista
        boolean[] enArbol = new boolean[n];
        for (int v = 0; v < n; v++) {
            clave[v] = Grafo.INFINITO;
            padre[v] = -1;
        }
        clave[origen] = 0;

        PriorityQueue<EnCola> cola = new PriorityQueue<>(Comparator.comparingDouble(EnCola::clave));
        cola.add(new EnCola(origen, 0));
        List<AristaMst> aristas = new ArrayList<>();
        double costoTotal = 0;

        while (!cola.isEmpty()) {
            int u = cola.poll().vertice();
            if (enArbol[u]) {
                continue; // entrada vieja: u ya entro al arbol por una arista mas barata
            }
            enArbol[u] = true; // la arista mas barata que cruza el corte entra al arbol
            if (padre[u] != -1) {
                aristas.add(new AristaMst(grafo.idDe(padre[u]), grafo.idDe(u), clave[u]));
                costoTotal += clave[u];
            }
            for (Grafo.Arista a : grafo.vecinos(u)) {
                int v = a.destino();
                if (!enArbol[v] && a.costo() < clave[v]) {
                    clave[v] = a.costo();
                    padre[v] = u;
                    cola.add(new EnCola(v, a.costo()));
                }
            }
        }

        validarConexo(aristas, n);
        return new Mst(aristas, costoTotal);
    }

    /** Vertice en la cola de prioridad con la clave que tenia al encolarlo. */
    private record EnCola(int vertice, double clave) { }

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
     *
     * Las aristas se ordenan con el MergeSort propio del hito 2: O(E log E), y
     * es lo que domina. Despues, E consultas a Union-Find casi O(1) cada una.
     */
    public Mst kruskal(Grafo grafo) {
        validarNoDirigido(grafo);
        int n = grafo.cantidadVertices();

        // En el grafo no dirigido cada ruta aparece en las dos listas: se toma solo con u < v
        List<AristaIndexada> todas = new ArrayList<>();
        for (int u = 0; u < n; u++) {
            for (Grafo.Arista a : grafo.vecinos(u)) {
                if (u < a.destino()) {
                    todas.add(new AristaIndexada(u, a.destino(), a.costo()));
                }
            }
        }
        List<AristaIndexada> porCosto =
                ordenamiento.mergeSort(todas, Comparator.comparingDouble(AristaIndexada::costo));

        UnionFind componentes = new UnionFind(n);
        List<AristaMst> aristas = new ArrayList<>();
        double costoTotal = 0;
        for (AristaIndexada e : porCosto) {
            if (aristas.size() == n - 1) {
                break; // ya es un arbol: el resto de las aristas solo formaria ciclos
            }
            if (componentes.find(e.u()) != componentes.find(e.v())) { // si no, cierra un ciclo
                componentes.union(e.u(), e.v());
                aristas.add(new AristaMst(grafo.idDe(e.u()), grafo.idDe(e.v()), e.costo()));
                costoTotal += e.costo();
            }
        }

        validarConexo(aristas, n);
        return new Mst(aristas, costoTotal);
    }

    private record AristaIndexada(int u, int v, double costo) { }

    /**
     * Union-Find (conjuntos disjuntos): cada componente es un arbol y su raiz la representa.
     * Con compresion de caminos y union por rango, find y union cuestan O(alfa(V))
     * amortizado (inversa de Ackermann): practicamente constante.
     */
    private static final class UnionFind {
        private final int[] padre;
        private final int[] rango;

        UnionFind(int n) {
            padre = new int[n];
            rango = new int[n];
            for (int i = 0; i < n; i++) {
                padre[i] = i; // cada vertice arranca en su propia componente
            }
        }

        int find(int x) {
            if (padre[x] != x) {
                padre[x] = find(padre[x]); // compresion de caminos: x queda colgado de la raiz
            }
            return padre[x];
        }

        void union(int a, int b) {
            int ra = find(a);
            int rb = find(b);
            if (rango[ra] < rango[rb]) { // el arbol mas bajo se cuelga del mas alto
                padre[ra] = rb;
            } else if (rango[ra] > rango[rb]) {
                padre[rb] = ra;
            } else {
                padre[rb] = ra;
                rango[ra]++;
            }
        }
    }

    private void validarNoDirigido(Grafo grafo) {
        if (grafo.esDirigido()) {
            throw new IllegalArgumentException("El arbol de recubrimiento minimo exige un grafo no dirigido");
        }
    }

    /** Un arbol que cubre V vertices tiene V - 1 aristas: si faltan, el grafo no es conexo. */
    private void validarConexo(List<AristaMst> aristas, int n) {
        if (aristas.size() != n - 1) {
            throw new IllegalStateException("El grafo no es conexo: un arbol de " + n
                    + " vertices necesita " + (n - 1) + " aristas y solo se pudieron agregar " + aristas.size());
        }
    }
}
