package uade.prog3.tpo.algorithm;

import org.springframework.stereotype.Component;
import uade.prog3.tpo.service.Grafo;

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
     */
    public List<Ruta> rutasSimples(Grafo grafo, String origenId, String destinoId,
                                   double costoMaximo, int saltosMaximos) {
        throw new PendienteDeImplementar("Backtracking de rutas simples");
    }
}
