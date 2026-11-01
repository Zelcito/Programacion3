package uade.prog3.tpo.algorithm;

import org.springframework.stereotype.Component;
import uade.prog3.tpo.service.Grafo;

import java.util.ArrayList;
import java.util.List;

/**
 * UNIDAD: Backtracking (clases 9 y 10)      PUNTAJE: 1 punto
 *
 * El problema propuesto es enumerar caminos simples bajo restricciones.
 * Cada grupo puede reemplazarlo por otro de su dominio, siempre que:
 *   - construya la solucion de forma incremental
 *   - tenga un criterio de viabilidad que pode ramas
 *   - deshaga la ultima decision al retroceder
 */
@Component
public class Backtracking {

    /** Un camino simple encontrado, con su costo acumulado. */
    public record Ruta(List<String> vertices, double costoTotal) { }

    /** Rutas encontradas y cuantas llamadas recursivas hizo la busqueda para encontrarlas. */
    public record Busqueda(List<Ruta> rutas, long nodosExplorados) { }

    /**
     * Enumerar TODOS los caminos simples de origen a destino que no superen
     * un costo maximo ni una cantidad maxima de saltos.
     *
     * Requisitos:
     *   - camino SIMPLE: no repetir vertices. Marcar y desmarcar el visitado
     *     es literalmente el retroceso del backtracking
     *   - podar en cuanto el costo parcial supere el maximo, sin esperar a
     *     completar el camino
     *   - devolver todas las rutas validas, no la primera
     *
     * Complejidad esperada: exponencial en el peor caso. Hay que poder
     * justificar por que, y explicar cuanto ayuda la poda en la practica.
     *
     * Sugerencia de verificacion: contar los nodos visitados con y sin poda
     * sobre el mismo grafo. Esa comparacion es material de coloquio.
     *
     * Implementacion: DFS recursivo que arma la ruta de a una ciudad. PAR -> JER
     * con 30 jornadas y 10 saltos encuentra 86 rutas explorando 4970 nodos; sin
     * poda explora 62509 (ver buscar).
     */
    public List<Ruta> rutasSimples(Grafo grafo, String origenId, String destinoId,
                                   double costoMaximo, int saltosMaximos) {
        return buscar(grafo, origenId, destinoId, costoMaximo, saltosMaximos, true).rutas();
    }

    /**
     * La misma busqueda, con o sin poda, contando los nodos explorados (cada
     * llamada recursiva). Sin poda recorre TODOS los caminos simples desde el
     * origen y recien al llegar al destino mira los topes: sirve para medir
     * cuanto ahorra la poda.
     */
    public Busqueda buscar(Grafo grafo, String origenId, String destinoId,
                           double costoMaximo, int saltosMaximos, boolean podar) {
        int origen = grafo.indiceDe(origenId);
        int destino = grafo.indiceDe(destinoId);
        if (costoMaximo < 0 || saltosMaximos < 0) {
            throw new IllegalArgumentException("costoMaximo y saltosMaximos no pueden ser negativos");
        }
        return new Explorador(grafo, destino, costoMaximo, saltosMaximos, podar).desde(origen);
    }

    /** Estado de una busqueda: lo que se marca al bajar y se desmarca al volver. */
    private static final class Explorador {
        private final Grafo grafo;
        private final int destino;
        private final double costoMaximo;
        private final int saltosMaximos;
        private final boolean podar;
        private final boolean[] visitado;
        private final List<String> camino = new ArrayList<>();
        private final List<Ruta> rutas = new ArrayList<>();
        private long nodosExplorados;

        Explorador(Grafo grafo, int destino, double costoMaximo, int saltosMaximos, boolean podar) {
            this.grafo = grafo;
            this.destino = destino;
            this.costoMaximo = costoMaximo;
            this.saltosMaximos = saltosMaximos;
            this.podar = podar;
            this.visitado = new boolean[grafo.cantidadVertices()];
        }

        Busqueda desde(int origen) {
            visitado[origen] = true;
            camino.add(grafo.idDe(origen));
            extender(origen, 0, 0);
            return new Busqueda(rutas, nodosExplorados);
        }

        /** La ruta parcial termina en u, cuesta costo y usa saltos rutas. */
        private void extender(int u, double costo, int saltos) {
            nodosExplorados++;
            boolean seExcede = costo > costoMaximo || saltos > saltosMaximos;
            if (podar && seExcede) {
                return; // PODA: con costos >= 0, seguir por aca solo suma mas costo y mas saltos
            }
            if (u == destino) {
                if (!seExcede) { // sin poda, los topes recien se miran aca
                    rutas.add(new Ruta(List.copyOf(camino), costo));
                }
                return; // seguir de largo repetiria el destino: ya no seria un camino simple
            }
            for (Grafo.Arista a : grafo.vecinos(u)) {
                int v = a.destino();
                if (!visitado[v]) {
                    visitado[v] = true; // decision: la ruta sigue por v
                    camino.add(grafo.idDe(v));
                    extender(v, costo + a.costo(), saltos + 1);
                    camino.remove(camino.size() - 1); // RETROCESO: se deshace la decision
                    visitado[v] = false;
                }
            }
        }
    }
}
