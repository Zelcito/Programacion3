package uade.prog3.tpo.algorithm;

import org.springframework.stereotype.Component;
import uade.prog3.tpo.model.Item;

import java.util.Comparator;
import java.util.List;

/**
 * UNIDAD: Divide y Conquista (clase 2)     PUNTAJE: 1 punto
 *
 * IMPORTANTE: no vale llamar a Collections.sort ni a Arrays.sort.
 * El punto se asigna por implementar el algoritmo, no por usarlo.
 */
@Component
public class Ordenamiento {

    /**
     * QuickSort sobre una lista de items.
     *
     * Requisitos:
     *   - implementar la particion (partition) a mano
     *   - documentar que estrategia de pivote usaron y por que
     *   - NO modificar la lista de entrada: devolver una lista nueva
     *
     * Complejidad esperada: O(n log n) promedio, O(n^2) peor caso.
     * Hay que poder explicar cuando ocurre el peor caso.
     */
    public List<Item> quickSort(List<Item> items, Comparator<Item> criterio) {
        throw new PendienteDeImplementar("QuickSort");
    }

    /**
     * MergeSort sobre una lista de items.
     *
     * Requisitos:
     *   - implementar la mezcla (merge) a mano
     *   - respetar la estabilidad del algoritmo
     *
     * Complejidad esperada: O(n log n) siempre.
     *
     * Para el coloquio: escribir la recurrencia T(n) = 2T(n/2) + O(n)
     * y resolverla con la regla practica (a, b, k).
     */
    public List<Item> mergeSort(List<Item> items, Comparator<Item> criterio) {
        throw new PendienteDeImplementar("MergeSort");
    }
}
