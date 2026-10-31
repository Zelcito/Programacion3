package uade.prog3.tpo;

import org.junit.jupiter.api.Test;
import uade.prog3.tpo.algorithm.Backtracking;
import uade.prog3.tpo.seed.CargaInicial;
import uade.prog3.tpo.service.Grafo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests del backtracking de rutas simples sobre La Ruta del Temple.
 *
 * Las cantidades estan calculadas con un script aparte que enumera todos los
 * caminos simples. Los nodos explorados no dependen del orden de los vecinos:
 * el arbol de busqueda es el mismo, solo cambia el orden en que se recorre.
 */
class BacktrackingTest {

    private final Backtracking backtracking = new Backtracking();

    private Grafo rutaDelTemple() {
        Grafo g = new Grafo(false);
        for (CargaInicial.Ciudad c : CargaInicial.CIUDADES) {
            g.agregarVertice(c.id());
        }
        for (CargaInicial.Ruta r : CargaInicial.RUTAS) {
            g.agregarArista(r.origen(), r.destino(), r.jornadas());
        }
        return g;
    }

    /** Empieza en origen, termina en destino, no repite ciudades, respeta los topes y suma bien. */
    private void assertEsRutaValida(Backtracking.Ruta ruta, String origen, String destino,
                                    double costoMaximo, int saltosMaximos) {
        Map<String, Integer> costoDeRuta = new HashMap<>();
        for (CargaInicial.Ruta r : CargaInicial.RUTAS) {
            costoDeRuta.put(r.origen() + "-" + r.destino(), r.jornadas());
            costoDeRuta.put(r.destino() + "-" + r.origen(), r.jornadas());
        }
        List<String> v = ruta.vertices();
        assertEquals(origen, v.get(0));
        assertEquals(destino, v.get(v.size() - 1));
        assertEquals(v.size(), new HashSet<>(v).size(), "repite ciudades: " + v);
        assertTrue(v.size() - 1 <= saltosMaximos, "demasiados saltos: " + v);
        assertTrue(ruta.costoTotal() <= costoMaximo, "demasiado cara: " + v);
        int suma = 0;
        for (int i = 1; i < v.size(); i++) {
            String tramo = v.get(i - 1) + "-" + v.get(i);
            assertTrue(costoDeRuta.containsKey(tramo), "no existe la ruta " + tramo);
            suma += costoDeRuta.get(tramo);
        }
        assertEquals(suma, ruta.costoTotal(), "costo mal sumado en " + v);
    }

    @Test
    void deParisAJerusalenHay86RutasDeHasta30JornadasY10Saltos() {
        List<Backtracking.Ruta> rutas = backtracking.rutasSimples(rutaDelTemple(), "PAR", "JER", 30, 10);
        assertEquals(86, rutas.size());
        Set<List<String>> distintas = new HashSet<>();
        for (Backtracking.Ruta r : rutas) {
            assertEsRutaValida(r, "PAR", "JER", 30, 10);
            distintas.add(r.vertices());
        }
        assertEquals(86, distintas.size(), "hay rutas repetidas");
    }

    @Test
    void conTopeDe26JornadasSoloQuedaLaRutaDeDijkstra() {
        List<Backtracking.Ruta> rutas = backtracking.rutasSimples(rutaDelTemple(), "PAR", "JER", 26, 10);
        assertEquals(1, rutas.size());
        assertEquals(List.of("PAR", "LYO", "MRS", "ROM", "BRI", "ATE", "ROD", "CHI", "JER"),
                rutas.get(0).vertices());
        assertEquals(26.0, rutas.get(0).costoTotal());
    }

    @Test
    void conTopeDe25JornadasNoHayNingunaRuta() {
        // 26 es el camino minimo: por debajo no hay nada
        assertTrue(backtracking.rutasSimples(rutaDelTemple(), "PAR", "JER", 25, 10).isEmpty());
    }

    @Test
    void conTopeDe7SaltosLaRutaDeDijkstraQuedaAfuera() {
        // la de 26 jornadas usa 8 saltos; con 7 quedan tres, de 27, 28 y 29 jornadas
        List<Backtracking.Ruta> rutas = backtracking.rutasSimples(rutaDelTemple(), "PAR", "JER", 30, 7);
        Set<Double> costos = new HashSet<>();
        for (Backtracking.Ruta r : rutas) {
            assertEsRutaValida(r, "PAR", "JER", 30, 7);
            costos.add(r.costoTotal());
        }
        assertEquals(3, rutas.size());
        assertEquals(Set.of(27.0, 28.0, 29.0), costos);
    }

    @Test
    void laPodaExploraUnOrdenDeMagnitudMenosNodos() {
        Grafo g = rutaDelTemple();
        Backtracking.Busqueda conPoda = backtracking.buscar(g, "PAR", "JER", 30, 10, true);
        Backtracking.Busqueda sinPoda = backtracking.buscar(g, "PAR", "JER", 30, 10, false);

        assertEquals(4970, conPoda.nodosExplorados());
        assertEquals(62509, sinPoda.nodosExplorados());
        // la poda no pierde ninguna ruta: sin poda se filtra al final y queda lo mismo
        assertEquals(new HashSet<>(sinPoda.rutas()), new HashSet<>(conPoda.rutas()));
    }

    @Test
    void origenIgualADestinoEsUnaRutaDeCostoCero() {
        List<Backtracking.Ruta> rutas = backtracking.rutasSimples(rutaDelTemple(), "PAR", "PAR", 30, 10);
        assertEquals(List.of(new Backtracking.Ruta(List.of("PAR"), 0)), rutas);
    }

    @Test
    void ciudadInexistenteFalla() {
        Grafo g = rutaDelTemple();
        assertThrows(IllegalArgumentException.class, () -> backtracking.rutasSimples(g, "XXX", "JER", 30, 10));
        assertThrows(IllegalArgumentException.class, () -> backtracking.rutasSimples(g, "PAR", "XXX", 30, 10));
    }

    @Test
    void topesNegativosSeRechazan() {
        Grafo g = rutaDelTemple();
        assertThrows(IllegalArgumentException.class, () -> backtracking.rutasSimples(g, "PAR", "JER", -1, 10));
        assertThrows(IllegalArgumentException.class, () -> backtracking.rutasSimples(g, "PAR", "JER", 30, -1));
    }
}
