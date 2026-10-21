package uade.prog3.tpo;

import org.junit.jupiter.api.Test;
import uade.prog3.tpo.algorithm.Ordenamiento;
import uade.prog3.tpo.model.Item;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de QuickSort. No tocan Neo4j: usan los mismos items de la carga inicial.
 */
class OrdenamientoTest {

    private final Ordenamiento ordenamiento = new Ordenamiento();

    private static final Comparator<Item> POR_RATIO = Comparator.comparingDouble(Item::ratioValorPeso);
    private static final Comparator<Item> POR_PESO = Comparator.comparingDouble(Item::getPeso);

    /** Los 5 items de la carga inicial. Ratios: I1=2, I2=10, I3=5, I4=16.7, I5=7.9 */
    private List<Item> itemsDeEjemplo() {
        return List.of(
                new Item("I1", "Item 1", 5, 10, "A"),
                new Item("I2", "Item 2", 4, 40, "B"),
                new Item("I3", "Item 3", 6, 30, "C"),
                new Item("I4", "Item 4", 3, 50, "D"),
                new Item("I5", "Item 5", 7, 55, "E"));
    }

    private List<String> ids(List<Item> items) {
        return items.stream().map(Item::getId).toList();
    }

    @Test
    void quickSortOrdenaPorRatio() {
        List<Item> ordenados = ordenamiento.quickSort(itemsDeEjemplo(), POR_RATIO);
        assertEquals(List.of("I1", "I3", "I5", "I2", "I4"), ids(ordenados));
    }

    @Test
    void quickSortOrdenaPorPeso() {
        List<Item> ordenados = ordenamiento.quickSort(itemsDeEjemplo(), POR_PESO);
        assertEquals(List.of("I4", "I2", "I1", "I3", "I5"), ids(ordenados));
    }

    @Test
    void quickSortNoModificaLaEntrada() {
        List<Item> entrada = new ArrayList<>(itemsDeEjemplo());
        ordenamiento.quickSort(entrada, POR_RATIO);
        assertEquals(List.of("I1", "I2", "I3", "I4", "I5"), ids(entrada));
    }

    @Test
    void quickSortCasosBorde() {
        assertTrue(ordenamiento.quickSort(List.of(), POR_RATIO).isEmpty());

        Item unico = new Item("X", "Unico", 1, 1, null);
        assertEquals(List.of(unico), ordenamiento.quickSort(List.of(unico), POR_RATIO));
    }

    @Test
    void quickSortConListaYaOrdenadaYRepetidos() {
        List<Item> entrada = List.of(
                new Item("a", "a", 1, 0, null),
                new Item("b", "b", 2, 0, null),
                new Item("c", "c", 2, 0, null),
                new Item("d", "d", 3, 0, null),
                new Item("e", "e", 3, 0, null));
        List<Double> pesos = ordenamiento.quickSort(entrada, POR_PESO).stream().map(Item::getPeso).toList();
        assertEquals(List.of(1.0, 2.0, 2.0, 3.0, 3.0), pesos);
    }
}
