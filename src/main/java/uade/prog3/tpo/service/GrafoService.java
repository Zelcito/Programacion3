package uade.prog3.tpo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uade.prog3.tpo.model.Conexion;
import uade.prog3.tpo.model.Nodo;
import uade.prog3.tpo.repository.NodoRepository;

import java.util.List;

/**
 * Carga el grafo desde Neo4j a memoria.
 *
 * INFRAESTRUCTURA: no forma parte de lo que hay que implementar.
 *
 * Decision de diseno importante para el trabajo: los algoritmos NO consultan
 * la base. Se carga el grafo una vez, se opera en memoria, y se devuelve el
 * resultado. Asi la complejidad temporal que se mide y se justifica en el
 * coloquio es la del algoritmo, no la de la red ni la del motor de base.
 */
@Service
public class GrafoService {

    private static final Logger log = LoggerFactory.getLogger(GrafoService.class);

    private final NodoRepository nodoRepository;

    public GrafoService(NodoRepository nodoRepository) {
        this.nodoRepository = nodoRepository;
    }

    /**
     * @param dirigido true si las relaciones CONECTA tienen sentido unico.
     *                 Para el arbol de recubrimiento minimo (Prim y Kruskal)
     *                 el grafo DEBE cargarse como NO dirigido.
     */
    public Grafo cargar(boolean dirigido) {
        Grafo grafo = new Grafo(dirigido);
        List<Nodo> nodos = nodoRepository.findAll();

        // 1) primero todos los vertices, para que queden en el grafo
        //    incluso los que no tienen ninguna arista
        for (Nodo n : nodos) {
            grafo.agregarVertice(n.getId());
        }

        // 2) despues las aristas, con su costo
        for (Nodo n : nodos) {
            for (Conexion c : n.getConexiones()) {
                grafo.agregarArista(n.getId(), c.getDestino().getId(), c.getCosto());
            }
        }

        log.info("Grafo cargado: {} vertices, {} aristas, dirigido={}",
                grafo.cantidadVertices(), grafo.cantidadAristas(), dirigido);
        return grafo;
    }
}
