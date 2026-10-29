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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Carga el dominio del grupo, La Ruta del Temple, en Neo4j (hito 4).
 *
 * Reemplaza al grafo de ejemplo del scaffold (nodos A..H): si la base tiene
 * datos pero no tiene Jerusalen, se borra todo y se carga el dominio.
 * Si Jerusalen ya esta, la carga no se repite.
 *
 * Los datos son los mismos del README:
 *   - 20 ciudades del Mediterraneo de 1129
 *   - 36 rutas con su costo en jornadas, todos enteros positivos
 *   - 9 provisiones para una mochila de 15 kg
 *
 * Cada ruta se guarda UNA sola vez en Neo4j, con la direccion de la tabla.
 * El grafo del dominio es no dirigido: los endpoints lo cargan con
 * cargar(false) y Grafo agrega la arista inversa en memoria.
 */
@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class CargaInicial implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CargaInicial.class);

    /** Ciudad destino del peregrino. Si esta en la base, el dominio ya fue cargado. */
    public static final String DESTINO = "JER";

    public record Ciudad(String id, String nombre, String tipo) { }

    public record Ruta(String origen, String destino, int jornadas) { }

    public static final List<Ciudad> CIUDADES = List.of(
            new Ciudad("PAR", "París", "CAPITAL"),
            new Ciudad("TRO", "Troyes", "CIUDAD"),
            new Ciudad("LYO", "Lyon", "CIUDAD"),
            new Ciudad("BCN", "Barcelona", "PUERTO"),
            new Ciudad("MRS", "Marsella", "PUERTO"),
            new Ciudad("GEN", "Génova", "PUERTO"),
            new Ciudad("VEN", "Venecia", "PUERTO"),
            new Ciudad("ROM", "Roma", "CAPITAL"),
            new Ciudad("PAL", "Palermo", "PUERTO"),
            new Ciudad("BRI", "Brindisi", "PUERTO"),
            new Ciudad("TES", "Tesalónica", "PUERTO"),
            new Ciudad("ATE", "Atenas", "PUERTO"),
            new Ciudad("CON", "Constantinopla", "CAPITAL"),
            new Ciudad("ICO", "Iconio", "CIUDAD"),
            new Ciudad("CRE", "Creta", "PUERTO"),
            new Ciudad("ROD", "Rodas", "PUERTO"),
            new Ciudad("CHI", "Chipre", "PUERTO"),
            new Ciudad("ANT", "Antioquía", "CIUDAD"),
            new Ciudad("TRI", "Trípoli", "PUERTO"),
            new Ciudad(DESTINO, "Jerusalén", "DESTINO")
    );

    /** Mismo orden que la tabla de rutas del README. El costo esta en jornadas. */
    public static final List<Ruta> RUTAS = List.of(
            new Ruta("PAR", "TRO", 2),
            new Ruta("PAR", "LYO", 3),
            new Ruta("TRO", "LYO", 2),
            new Ruta("LYO", "MRS", 2),
            new Ruta("LYO", "GEN", 4),
            new Ruta("MRS", "BCN", 3),
            new Ruta("MRS", "GEN", 3),
            new Ruta("BCN", "PAL", 6),
            new Ruta("MRS", "ROM", 4),
            new Ruta("GEN", "VEN", 3),
            new Ruta("GEN", "ROM", 3),
            new Ruta("VEN", "ROM", 3),
            new Ruta("ROM", "PAL", 3),
            new Ruta("ROM", "BRI", 4),
            new Ruta("VEN", "BRI", 4),
            new Ruta("PAL", "CRE", 7),
            new Ruta("PAL", "BRI", 3),
            new Ruta("BRI", "TES", 5),
            new Ruta("BRI", "ATE", 4),
            new Ruta("TES", "ATE", 3),
            new Ruta("TES", "CON", 4),
            new Ruta("ATE", "CRE", 2),
            new Ruta("ATE", "ROD", 3),
            new Ruta("ATE", "CON", 4),
            new Ruta("CON", "ICO", 4),
            new Ruta("CON", "ROD", 4),
            new Ruta("CRE", "ROD", 2),
            new Ruta("ROD", "CHI", 3),
            new Ruta("ICO", "ANT", 4),
            new Ruta("ICO", "ROD", 4),
            new Ruta("CHI", "TRI", 2),
            new Ruta("CHI", "ANT", 3),
            new Ruta("ANT", "TRI", 2),
            new Ruta("TRI", DESTINO, 3),
            new Ruta("CHI", DESTINO, 3),
            new Ruta("CRE", "CHI", 5)
    );

    /**
     * Provisiones para la mochila de 15 kg: peso en kg, valor en jornadas de viveres.
     *
     * Calibrado a proposito: greedy por valor/peso junta 19 jornadas en 13 kg,
     * mientras que el optimo con programacion dinamica es {PAN, CAR, ODR, VIN}
     * con 22 jornadas en 15 kg. Es el contraejemplo del hito 6.
     */
    public static final List<Item> PROVISIONES = List.of(
            new Item("PAN", "Pan de centeno", 2, 2, null),
            new Item("CAR", "Carne salada", 3, 6, null),
            new Item("QUE", "Queso curado", 4, 5, null),
            new Item("ODR", "Odre de agua", 2, 4, null),
            new Item("VIN", "Vino aguado", 8, 10, null),
            new Item("REL", "Reliquia santa", 4, 2, null),
            new Item("SAL", "Salterio", 2, 2, null),
            new Item("ESP", "Espada", 5, 3, null),
            new Item("UNG", "Ungüentos", 3, 2, null)
    );

    private final NodoRepository nodoRepository;
    private final ItemRepository itemRepository;

    public CargaInicial(NodoRepository nodoRepository, ItemRepository itemRepository) {
        this.nodoRepository = nodoRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public void run(String... args) {
        if (nodoRepository.existsById(DESTINO)) {
            log.info("La Ruta del Temple ya esta cargada. No se ejecuta la carga inicial.");
            return;
        }

        if (nodoRepository.count() > 0 || itemRepository.count() > 0) {
            log.info("La base tiene datos que no son del dominio (grafo de ejemplo). Se borran.");
            nodoRepository.deleteAll();
            itemRepository.deleteAll();
        }

        Map<String, Nodo> porId = new HashMap<>();
        List<Nodo> nodos = new ArrayList<>();
        for (Ciudad c : CIUDADES) {
            Nodo n = new Nodo(c.id(), c.nombre(), c.tipo(), 0);
            porId.put(c.id(), n);
            nodos.add(n);
        }
        for (Ruta r : RUTAS) {
            porId.get(r.origen()).conectar(porId.get(r.destino()), r.jornadas());
        }

        nodoRepository.saveAll(nodos);
        itemRepository.saveAll(PROVISIONES);

        log.info("La Ruta del Temple cargada: {} ciudades, {} rutas, {} provisiones.",
                CIUDADES.size(), RUTAS.size(), PROVISIONES.size());
        log.info("Para desactivar la carga: app.seed.enabled=false");
    }
}
