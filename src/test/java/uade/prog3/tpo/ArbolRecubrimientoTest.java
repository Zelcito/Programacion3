package uade.prog3.tpo;

import org.junit.jupiter.api.Test;
import uade.prog3.tpo.algorithm.ArbolRecubrimiento;
import uade.prog3.tpo.algorithm.Recorridos;
import uade.prog3.tpo.seed.CargaInicial;
import uade.prog3.tpo.service.Grafo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de Prim y Kruskal sobre La Ruta del Temple y sobre el grafo A..H de GUIA-TPO.md.
 *
 * Con pesos repetidos puede haber varios arboles minimos: los tests no fijan
 * el conjunto de aristas, sino lo que vale para cualquier MST (costo total,
 * V - 1 aristas reales del grafo que conectan todas las ciudades).
 */
class ArbolRecubrimientoTest {

    private final ArbolRecubrimiento arbol = new ArbolRecubrimiento();

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

    /** Grafo de ejemplo del scaffold (GUIA-TPO.md): MST de costo 27 con 7 aristas. */
    private Grafo grafoDeEjemplo() {
        Grafo g = new Grafo(false);
        g.agregarArista("A", "B", 4);
        g.agregarArista("A", "C", 9);
        g.agregarArista("B", "C", 3);
        g.agregarArista("B", "D", 7);
        g.agregarArista("C", "D", 2);
        g.agregarArista("C", "E", 11);
        g.agregarArista("D", "E", 5);
        g.agregarArista("D", "F", 8);
        g.agregarArista("E", "F", 6);
        g.agregarArista("E", "G", 10);
        g.agregarArista("F", "H", 3);
        g.agregarArista("G", "H", 4);
        return g;
    }

    /** Las 19 aristas existen en la tabla de rutas con ese costo y unen las 20 ciudades. */
    private void assertEsArbolDeLaRutaDelTemple(ArbolRecubrimiento.Mst mst) {
        assertEquals(19, mst.aristas().size());

        Map<String, Integer> costoDeRuta = new HashMap<>();
        for (CargaInicial.Ruta r : CargaInicial.RUTAS) {
            costoDeRuta.put(r.origen() + "-" + r.destino(), r.jornadas());
            costoDeRuta.put(r.destino() + "-" + r.origen(), r.jornadas());
        }
        Grafo soloElArbol = new Grafo(false);
        double suma = 0;
        for (ArbolRecubrimiento.AristaMst a : mst.aristas()) {
            String clave = a.origen() + "-" + a.destino();
            assertTrue(costoDeRuta.containsKey(clave), "no existe la ruta " + clave);
            assertEquals(costoDeRuta.get(clave), (int) a.costo(), "costo de " + clave);
            soloElArbol.agregarArista(a.origen(), a.destino(), a.costo());
            suma += a.costo();
        }
        assertEquals(suma, mst.costoTotal());
        // 19 aristas que conectan 20 vertices no pueden tener ciclos: es un arbol
        assertEquals(20, new Recorridos().bfs(soloElArbol, "PAR").size());
    }

    /** Cuantas aristas de cada costo tiene el arbol. Es igual para todos los MST de un grafo. */
    private Map<Double, Integer> costosPorCantidad(ArbolRecubrimiento.Mst mst) {
        Map<Double, Integer> cantidad = new HashMap<>();
        for (ArbolRecubrimiento.AristaMst a : mst.aristas()) {
            cantidad.merge(a.costo(), 1, Integer::sum);
        }
        return cantidad;
    }

    @Test
    void primDesdeParisCuesta53Con19Aristas() {
        ArbolRecubrimiento.Mst mst = arbol.prim(rutaDelTemple(), "PAR");
        assertEquals(53.0, mst.costoTotal());
        assertEsArbolDeLaRutaDelTemple(mst);
    }

    @Test
    void kruskalCuesta53Con19Aristas() {
        ArbolRecubrimiento.Mst mst = arbol.kruskal(rutaDelTemple());
        assertEquals(53.0, mst.costoTotal());
        assertEsArbolDeLaRutaDelTemple(mst);
    }

    @Test
    void primYKruskalUsanSieteRutasDe2NueveDe3YTresDe4() {
        Map<Double, Integer> esperado = Map.of(2.0, 7, 3.0, 9, 4.0, 3);
        assertEquals(esperado, costosPorCantidad(arbol.prim(rutaDelTemple(), "PAR")));
        assertEquals(esperado, costosPorCantidad(arbol.kruskal(rutaDelTemple())));
    }

    @Test
    void primDaElMismoCostoDesdeCualquierCiudad() {
        Grafo g = rutaDelTemple();
        for (String origen : g.ids()) {
            assertEquals(53.0, arbol.prim(g, origen).costoTotal(), "Prim desde " + origen);
        }
    }

    @Test
    void elGrafoDeEjemploTieneUnMstDe27Con7Aristas() {
        ArbolRecubrimiento.Mst porPrim = arbol.prim(grafoDeEjemplo(), "A");
        ArbolRecubrimiento.Mst porKruskal = arbol.kruskal(grafoDeEjemplo());
        assertEquals(27.0, porPrim.costoTotal());
        assertEquals(7, porPrim.aristas().size());
        assertEquals(27.0, porKruskal.costoTotal());
        assertEquals(7, porKruskal.aristas().size());
    }

    @Test
    void grafoNoConexoSeRechaza() {
        Grafo g = rutaDelTemple();
        g.agregarVertice("ISL");
        assertThrows(IllegalStateException.class, () -> arbol.prim(g, "PAR"));
        assertThrows(IllegalStateException.class, () -> arbol.kruskal(g));
    }

    @Test
    void primConOrigenInexistenteFalla() {
        assertThrows(IllegalArgumentException.class, () -> arbol.prim(rutaDelTemple(), "XXX"));
    }

    @Test
    void grafoDirigidoSeRechaza() {
        Grafo g = new Grafo(true);
        g.agregarArista("A", "B", 1);
        g.agregarArista("B", "A", 1);
        assertThrows(IllegalArgumentException.class, () -> arbol.prim(g, "A"));
        assertThrows(IllegalArgumentException.class, () -> arbol.kruskal(g));
    }
}
