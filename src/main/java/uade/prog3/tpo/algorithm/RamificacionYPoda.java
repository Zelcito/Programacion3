package uade.prog3.tpo.algorithm;

import org.springframework.stereotype.Component;
import uade.prog3.tpo.model.Item;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
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
    public record Asignacion(Map<String, List<Item>> porContenedor, double cargaMaxima,
                             long nodosExplorados, long nodosPodados) { }

    private final Ordenamiento ordenamiento = new Ordenamiento();

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
     *   - la cota tiene que ser OPTIMISTA. Sobreestimar el costo minimo de una
     *     rama puede descartar una solucion mejor.
     *
     * El arbol de busqueda tiene hasta k^n hojas. Se cuentan todas las
     * llamadas, incluida la raiz y las que se cortan por la cota.
     */
    public Asignacion repartir(List<Item> items, int cantidadContenedores) {
        if (cantidadContenedores < 1) {
            throw new IllegalArgumentException("La cantidad de contenedores debe ser al menos 1");
        }
        for (Item item : items) {
            if (!Double.isFinite(item.getPeso()) || item.getPeso() < 0) {
                throw new IllegalArgumentException("El item " + item.getId() + " debe tener peso finito y no negativo");
            }
        }
        List<Item> ordenados = ordenamiento.mergeSort(items, Comparator.comparingDouble(Item::getPeso).reversed());
        return new Busqueda(ordenados, cantidadContenedores).resolver();
    }

    /** Estado de una busqueda: las cargas y decisiones se deshacen al volver. */
    private static final class Busqueda {
        private final List<Item> items;
        private final BigDecimal[] pesos;
        private final BigDecimal[] cargas;
        private final BigDecimal cantidadMulas;
        private final BigDecimal pesoTotal;
        private final int[] asignacion;
        private int[] mejorAsignacion;
        private BigDecimal mejorCarga;
        private long nodosExplorados;
        private long nodosPodados;

        Busqueda(List<Item> items, int cantidadContenedores) {
            this.items = items;
            this.cargas = new BigDecimal[cantidadContenedores];
            Arrays.fill(cargas, BigDecimal.ZERO);
            this.cantidadMulas = BigDecimal.valueOf(cantidadContenedores);
            this.asignacion = new int[items.size()];
            this.pesos = new BigDecimal[items.size()];
            BigDecimal total = BigDecimal.ZERO;
            // Sumas y comparaciones decimales exactas: un redondeo no puede subir la cota.
            for (int i = 0; i < items.size(); i++) {
                pesos[i] = BigDecimal.valueOf(items.get(i).getPeso());
                total = total.add(pesos[i]);
            }
            this.pesoTotal = total;
        }

        Asignacion resolver() {
            asignar(0, BigDecimal.ZERO);
            double cargaMaxima = mejorCarga.doubleValue();
            if (!Double.isFinite(cargaMaxima)) {
                throw new IllegalArgumentException("La carga maxima supera el rango representable");
            }
            Map<String, List<Item>> porContenedor = new LinkedHashMap<>();
            for (int mula = 0; mula < cargas.length; mula++) {
                porContenedor.put("Mula " + (mula + 1), new ArrayList<>());
            }
            for (int i = 0; i < items.size(); i++) {
                porContenedor.get("Mula " + (mejorAsignacion[i] + 1)).add(items.get(i));
            }
            return new Asignacion(porContenedor, cargaMaxima, nodosExplorados, nodosPodados);
        }

        private void asignar(int indice, BigDecimal cargaMaxima) {
            nodosExplorados++;
            // PODA: max(cargaMaxima, pesoTotal / k) >= mejorCarga.
            // Comparar pesoTotal con mejorCarga * k evita redondear la division.
            if (mejorCarga != null && (cargaMaxima.compareTo(mejorCarga) >= 0
                    || pesoTotal.compareTo(mejorCarga.multiply(cantidadMulas)) >= 0)) {
                nodosPodados++;
                return;
            }
            if (indice == items.size()) {
                mejorCarga = cargaMaxima;
                mejorAsignacion = asignacion.clone();
                return;
            }
            for (int mula = 0; mula < cargas.length; mula++) {
                BigDecimal cargaAnterior = cargas[mula];
                cargas[mula] = cargaAnterior.add(pesos[indice]);
                asignacion[indice] = mula;
                asignar(indice + 1, cargaMaxima.max(cargas[mula]));
                cargas[mula] = cargaAnterior; // RETROCESO: la proxima rama prueba otra mula
            }
        }
    }
}
