package uade.prog3.tpo.seed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import uade.prog3.tpo.model.Item;
import uade.prog3.tpo.model.Nodo;
import uade.prog3.tpo.repository.ItemRepository;
import uade.prog3.tpo.repository.NodoRepository;

import java.util.List;

/**
 * Carga un grafo chico de ejemplo para que la aplicacion sea probable
 * apenas se levanta, sin tener que cargar datos a mano.
 *
 * El dominio es DELIBERADAMENTE generico (nodos A..H). Cada grupo lo
 * reemplaza por el suyo y pone app.seed.enabled=false, o bien reescribe
 * esta clase con sus propios datos.
 *
 * El grafo elegido tiene, a proposito:
 *   - 8 vertices y 12 aristas: chico para verificar a mano, grande para
 *     que los algoritmos no den resultados triviales
 *   - todos los costos positivos, para que Dijkstra sea aplicable
 *   - es conexo, para que Prim y Kruskal tengan solucion
 *   - hay al menos un camino donde el mas corto NO es el directo, para
 *     que se note la relajacion de aristas
 */
@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class CargaInicial implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CargaInicial.class);

    private final NodoRepository nodoRepository;
    private final ItemRepository itemRepository;

    public CargaInicial(NodoRepository nodoRepository, ItemRepository itemRepository) {
        this.nodoRepository = nodoRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public void run(String... args) {
        if (nodoRepository.count() > 0) {
            log.info("La base ya tiene datos. No se ejecuta la carga inicial.");
            return;
        }

        Nodo a = new Nodo("A", "Nodo A", "CENTRO", 0);
        Nodo b = new Nodo("B", "Nodo B", "PUNTO", 12);
        Nodo c = new Nodo("C", "Nodo C", "PUNTO", 8);
        Nodo d = new Nodo("D", "Nodo D", "PUNTO", 15);
        Nodo e = new Nodo("E", "Nodo E", "PUNTO", 6);
        Nodo f = new Nodo("F", "Nodo F", "PUNTO", 20);
        Nodo g = new Nodo("G", "Nodo G", "PUNTO", 9);
        Nodo h = new Nodo("H", "Nodo H", "PUNTO", 4);

        // El camino directo A->C cuesta 9, pero A->B->C cuesta 4+3=7.
        // Sirve para verificar que la relajacion de aristas este bien hecha.
        a.conectar(b, 4);
        a.conectar(c, 9);
        b.conectar(c, 3);
        b.conectar(d, 7);
        c.conectar(d, 2);
        c.conectar(e, 11);
        d.conectar(e, 5);
        d.conectar(f, 8);
        e.conectar(f, 6);
        e.conectar(g, 10);
        f.conectar(h, 3);
        g.conectar(h, 4);

        nodoRepository.saveAll(List.of(a, b, c, d, e, f, g, h));

        // Items para los problemas de seleccion bajo restriccion.
        //
        // Calibrado a proposito: con capacidad 10, greedy por ratio valor/peso
        // elige {I4, I2} y obtiene 90, mientras que el optimo real es {I4, I5}
        // con 105. Es el contraejemplo que se necesita para comparar greedy
        // contra programacion dinamica.
        itemRepository.saveAll(List.of(
                new Item("I1", "Item 1", 5, 10, "A"),
                new Item("I2", "Item 2", 4, 40, "B"),
                new Item("I3", "Item 3", 6, 30, "C"),
                new Item("I4", "Item 4", 3, 50, "D"),
                new Item("I5", "Item 5", 7, 55, "E")
        ));

        log.info("Carga inicial completada: 8 nodos, 12 aristas, 5 items.");
        log.info("Para desactivarla: app.seed.enabled=false");
    }
}
