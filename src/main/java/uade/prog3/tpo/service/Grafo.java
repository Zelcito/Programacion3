package uade.prog3.tpo.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Grafo ponderado en memoria, listo para correr algoritmos encima.
 *
 * Esta clase es INFRAESTRUCTURA, no es parte del trabajo a implementar:
 * su unico proposito es que no tengan que pelear con Neo4j mientras
 * escriben Dijkstra. Se carga una vez desde la base y despues se opera
 * sobre estructuras de datos comunes.
 *
 * Representacion: lista de adyacencia. Los vertices se indexan de 0 a n-1
 * y se puede traducir en ambos sentidos entre indice e id de negocio.
 */
public class Grafo {

    /** Arista dirigida hacia un vertice destino, con su costo. */
    public record Arista(int destino, double costo) { }

    private final List<String> ids = new ArrayList<>();
    private final Map<String, Integer> indicePorId = new HashMap<>();
    private final List<List<Arista>> adyacencia = new ArrayList<>();
    private final boolean dirigido;

    public Grafo(boolean dirigido) {
        this.dirigido = dirigido;
    }

    /** Agrega un vertice si no existe y devuelve su indice. */
    public int agregarVertice(String id) {
        Integer existente = indicePorId.get(id);
        if (existente != null) {
            return existente;
        }
        int indice = ids.size();
        ids.add(id);
        indicePorId.put(id, indice);
        adyacencia.add(new ArrayList<>());
        return indice;
    }

    /** Agrega una arista. Si el grafo es no dirigido, agrega tambien la inversa. */
    public void agregarArista(String origenId, String destinoId, double costo) {
        int u = agregarVertice(origenId);
        int v = agregarVertice(destinoId);
        adyacencia.get(u).add(new Arista(v, costo));
        if (!dirigido) {
            adyacencia.get(v).add(new Arista(u, costo));
        }
    }

    public int cantidadVertices() {
        return ids.size();
    }

    public int cantidadAristas() {
        int total = 0;
        for (List<Arista> vecinos : adyacencia) {
            total += vecinos.size();
        }
        return dirigido ? total : total / 2;
    }

    public boolean esDirigido() {
        return dirigido;
    }

    /** Vecinos del vertice, en formato lista de adyacencia. */
    public List<Arista> vecinos(int vertice) {
        return Collections.unmodifiableList(adyacencia.get(vertice));
    }

    public int indiceDe(String id) {
        Integer indice = indicePorId.get(id);
        if (indice == null) {
            throw new IllegalArgumentException("No existe el nodo con id: " + id);
        }
        return indice;
    }

    public String idDe(int indice) {
        return ids.get(indice);
    }

    public List<String> ids() {
        return Collections.unmodifiableList(ids);
    }

    /**
     * Matriz de adyacencia con costos. Sin arista se representa con INFINITO
     * y la diagonal vale 0. Es la representacion que necesita Floyd-Warshall.
     */
    public double[][] matrizDeAdyacencia() {
        int n = cantidadVertices();
        double[][] m = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                m[i][j] = (i == j) ? 0.0 : INFINITO;
            }
        }
        for (int u = 0; u < n; u++) {
            for (Arista a : adyacencia.get(u)) {
                // si hay aristas paralelas, se conserva la de menor costo
                m[u][a.destino()] = Math.min(m[u][a.destino()], a.costo());
            }
        }
        return m;
    }

    public static final double INFINITO = Double.POSITIVE_INFINITY;
}
