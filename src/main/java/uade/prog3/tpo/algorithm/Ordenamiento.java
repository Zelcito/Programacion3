package uade.prog3.tpo.algorithm;

import org.springframework.stereotype.Component;
import uade.prog3.tpo.model.Item;

import java.util.ArrayList;
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
     *
     * Pivote: el elemento del medio del rango. Con el ultimo elemento como
     * pivote, una lista ya ordenada (caso comun) cae siempre en el peor caso,
     * porque cada particion deja un lado vacio y el otro con n-1 elementos.
     * Con el del medio, una lista ordenada se parte en dos mitades parejas.
     * El peor caso sigue existiendo (pivote siempre minimo o maximo del rango),
     * pero necesita una entrada armada a proposito.
     *
     * Particion: Lomuto. Espacio extra O(n) por la copia de la lista,
     * mas O(log n) promedio de pila por la recursion.
     */
    public List<Item> quickSort(List<Item> items, Comparator<Item> criterio) {
        List<Item> copia = new ArrayList<>(items);
        quickSort(copia, 0, copia.size() - 1, criterio);
        return copia;
    }

    private void quickSort(List<Item> lista, int izq, int der, Comparator<Item> criterio) {
        if (izq >= der) {
            return; // rango de 0 o 1 elementos: ya esta ordenado
        }
        int p = particionar(lista, izq, der, criterio);
        quickSort(lista, izq, p - 1, criterio);
        quickSort(lista, p + 1, der, criterio);
    }

    /**
     * Deja a la izquierda de la posicion devuelta los elementos menores o
     * iguales al pivote, y a la derecha los mayores. El pivote queda en su
     * posicion definitiva. O(n) sobre el rango [izq, der].
     */
    private int particionar(List<Item> lista, int izq, int der, Comparator<Item> criterio) {
        int medio = izq + (der - izq) / 2;
        swap(lista, medio, der);
        Item pivote = lista.get(der);

        int i = izq - 1; // fin de la zona de elementos <= pivote
        for (int j = izq; j < der; j++) {
            if (criterio.compare(lista.get(j), pivote) <= 0) {
                i++;
                swap(lista, i, j);
            }
        }
        swap(lista, i + 1, der);
        return i + 1;
    }

    private void swap(List<Item> lista, int a, int b) {
        Item tmp = lista.get(a);
        lista.set(a, lista.get(b));
        lista.set(b, tmp);
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
     *
     * Recurrencia: T(n) = 2T(n/2) + O(n), con a = 2, b = 2, k = 1.
     * Como a = b^k, T(n) = O(n^k log n) = O(n log n), en todos los casos:
     * la division no depende de los datos, siempre parte por la mitad.
     *
     * Estabilidad: en la mezcla, ante un empate se toma primero el elemento
     * de la mitad izquierda (compare <= 0), que venia antes en la entrada.
     *
     * Espacio extra: O(n) por las listas auxiliares de la mezcla.
     */
    public List<Item> mergeSort(List<Item> items, Comparator<Item> criterio) {
        if (items.size() <= 1) {
            return new ArrayList<>(items); // caso base, y tambien copia la entrada
        }
        int medio = items.size() / 2;
        List<Item> izquierda = mergeSort(items.subList(0, medio), criterio);
        List<Item> derecha = mergeSort(items.subList(medio, items.size()), criterio);
        return mezclar(izquierda, derecha, criterio);
    }

    /** Une dos listas ya ordenadas en una nueva lista ordenada. O(n). */
    private List<Item> mezclar(List<Item> izquierda, List<Item> derecha, Comparator<Item> criterio) {
        List<Item> resultado = new ArrayList<>(izquierda.size() + derecha.size());
        int i = 0;
        int j = 0;
        while (i < izquierda.size() && j < derecha.size()) {
            if (criterio.compare(izquierda.get(i), derecha.get(j)) <= 0) {
                resultado.add(izquierda.get(i++));
            } else {
                resultado.add(derecha.get(j++));
            }
        }
        // Una de las dos se termino: lo que queda de la otra ya esta ordenado.
        while (i < izquierda.size()) {
            resultado.add(izquierda.get(i++));
        }
        while (j < derecha.size()) {
            resultado.add(derecha.get(j++));
        }
        return resultado;
    }
}
