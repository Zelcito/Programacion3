package uade.prog3.tpo.algorithm;

import org.springframework.stereotype.Component;
import uade.prog3.tpo.model.Item;

import java.util.List;
import java.util.Map;

/**
 * UNIDAD: Branch and Bound (clase 12)       PUNTAJE: 1 punto
 *
 * Es backtracking mas una cota. La diferencia con la clase Backtracking
 * es el criterio de poda: alli se podaba por VIABILIDAD, aca se poda por
 * OPTIMALIDAD.
 */
@Component
public class RamificacionYPoda {

    /** Asignacion de items a contenedores o responsables. */
    public record Asignacion(Map<String, List<Item>> porContenedor, double cargaMaxima) { }

    /**
     * Repartir items entre k contenedores identicos minimizando la carga del
     * contenedor mas cargado (problema de makespan).
     *
     * Requisitos:
     *   - ramificar: cada item puede ir a cualquiera de los k contenedores
     *   - acotar: definir una cota INFERIOR del makespan alcanzable desde el
     *     nodo actual. Una cota valida y simple es
     *         max( carga maxima actual , suma de todos los pesos / k )
     *   - podar: si la cota es mayor o igual al mejor makespan ya encontrado,
     *     abandonar la rama
     *   - la cota tiene que ser OPTIMISTA. Si subestima el potencial de una
     *     rama se pierden soluciones validas y el resultado deja de ser optimo
     *
     * Complejidad esperada: exponencial, k^n en el peor caso, mucho menor con
     * buena poda.
     *
     * Sugerencia: reportar en la respuesta cuantos nodos se exploraron y
     * cuantos se podaron. Es la mejor evidencia de que la poda funciona.
     */
    public Asignacion repartir(List<Item> items, int cantidadContenedores) {
        throw new PendienteDeImplementar("Branch and Bound de reparto");
    }
}
