package uade.prog3.tpo.controller;

import org.springframework.web.bind.annotation.*;
import uade.prog3.tpo.algorithm.Ordenamiento;
import uade.prog3.tpo.algorithm.RamificacionYPoda;
import uade.prog3.tpo.algorithm.Seleccion;
import uade.prog3.tpo.model.Item;
import uade.prog3.tpo.repository.ItemRepository;

import java.util.Comparator;
import java.util.List;

/**
 * Endpoints de seleccion, ordenamiento y reparto.
 * Igual que en GrafoController: la logica va en el paquete algorithm.
 */
@RestController
@RequestMapping("/api/seleccion")
public class SeleccionController {

    private final ItemRepository itemRepository;
    private final Seleccion seleccion;
    private final Ordenamiento ordenamiento;
    private final RamificacionYPoda ramificacionYPoda;

    public SeleccionController(ItemRepository itemRepository,
                               Seleccion seleccion,
                               Ordenamiento ordenamiento,
                               RamificacionYPoda ramificacionYPoda) {
        this.itemRepository = itemRepository;
        this.seleccion = seleccion;
        this.ordenamiento = ordenamiento;
        this.ramificacionYPoda = ramificacionYPoda;
    }

    private List<Item> todos() {
        return itemRepository.findAll();
    }

    /** GET /api/seleccion/greedy?capacidad=10 */
    @GetMapping("/greedy")
    public Seleccion.Resultado greedy(@RequestParam double capacidad) {
        return seleccion.greedy(todos(), capacidad);
    }

    /** GET /api/seleccion/dinamica?capacidad=10 */
    @GetMapping("/dinamica")
    public Seleccion.Resultado dinamica(@RequestParam int capacidad) {
        return seleccion.programacionDinamica(todos(), capacidad);
    }

    /**
     * GET /api/seleccion/quicksort?criterio=valor
     * criterio admite: valor | peso | ratio
     */
    @GetMapping("/quicksort")
    public List<Item> quickSort(@RequestParam(defaultValue = "ratio") String criterio) {
        return ordenamiento.quickSort(todos(), comparador(criterio));
    }

    /** GET /api/seleccion/mergesort?criterio=peso */
    @GetMapping("/mergesort")
    public List<Item> mergeSort(@RequestParam(defaultValue = "ratio") String criterio) {
        return ordenamiento.mergeSort(todos(), comparador(criterio));
    }

    /** GET /api/seleccion/repartir?contenedores=3 */
    @GetMapping("/repartir")
    public RamificacionYPoda.Asignacion repartir(@RequestParam int contenedores) {
        if (contenedores < 1) {
            throw new IllegalArgumentException("La cantidad de contenedores debe ser al menos 1");
        }
        return ramificacionYPoda.repartir(todos(), contenedores);
    }

    private Comparator<Item> comparador(String criterio) {
        return switch (criterio) {
            case "valor" -> Comparator.comparingDouble(Item::getValor);
            case "peso"  -> Comparator.comparingDouble(Item::getPeso);
            case "ratio" -> Comparator.comparingDouble(Item::ratioValorPeso);
            default -> throw new IllegalArgumentException(
                    "Criterio invalido: " + criterio + ". Use valor, peso o ratio.");
        };
    }
}
