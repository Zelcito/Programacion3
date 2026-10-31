package uade.prog3.tpo.algorithm;

import org.springframework.stereotype.Component;
import uade.prog3.tpo.model.Item;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * UNIDAD: Greedy (clase 3) y Programacion Dinamica (clase 6)
 * PUNTAJE: 1 punto greedy + 1 punto programacion dinamica
 *
 * Los dos metodos resuelven EL MISMO problema con tecnicas distintas.
 * Esa comparacion es justamente lo que se evalua: no alcanza con que
 * funcionen, hay que poder mostrar un caso donde greedy NO da el optimo.
 *
 * En el dominio: elegir que provisiones lleva el mensajero en la mochila.
 * El peso son kg y el valor son jornadas de viveres.
 */
@Component
public class Seleccion {

    /** Resultado de una seleccion de items bajo restriccion de capacidad. */
    public record Resultado(List<Item> elegidos, double valorTotal, double pesoTotal, String tecnica) { }

    // Ordenamiento propio del hito 2: no se usa ningun sort de libreria
    private final Ordenamiento ordenamiento = new Ordenamiento();

    /**
     * GREEDY: seleccionar items maximizando el valor total sin superar la capacidad,
     * ordenando por ratio valor/peso descendente y tomando mientras entren.
     *
     * Requisitos:
     *   - declarar EXPLICITAMENTE cual es la funcion objetivo
     *   - usar el ratio valor/peso como criterio, no el valor a secas
     *   - documentar la complejidad, que esta dominada por el ordenamiento
     *
     * Complejidad esperada: O(n log n).
     *
     * OJO: sobre items indivisibles (mochila 0/1) este algoritmo NO garantiza
     * el optimo. Eso no es un error a corregir: es el punto de la comparacion.
     * En el coloquio hay que traer un juego de datos donde greedy pierda.
     *
     * Funcion objetivo: maximizar la suma de valores de los elegidos,
     * sujeto a que la suma de pesos no supere la capacidad.
     *
     * Criterio greedy: ratio valor/peso descendente. A igual ratio, primero el
     * mas liviano (QUE y VIN empatan en 1,25: asi el resultado no depende del
     * orden en que vengan de Neo4j). MergeSort es estable, asi que si tambien
     * empatan en peso (PAN y SAL) queda el orden de entrada.
     *
     * Complejidad: O(n log n) del MergeSort + O(n) de la pasada = O(n log n).
     * Espacio: O(n) por la lista ordenada.
     */
    public Resultado greedy(List<Item> items, double capacidad) {
        validar(items, capacidad);

        Comparator<Item> porRatioDescYPesoAsc = (a, b) -> {
            int porRatio = Double.compare(b.ratioValorPeso(), a.ratioValorPeso());
            return porRatio != 0 ? porRatio : Double.compare(a.getPeso(), b.getPeso());
        };
        List<Item> ordenados = ordenamiento.mergeSort(items, porRatioDescYPesoAsc);

        List<Item> elegidos = new ArrayList<>();
        double peso = 0;
        double valor = 0;
        for (Item item : ordenados) {
            // eleccion greedy: si entra se toma y no se revisa nunca mas
            if (peso + item.getPeso() <= capacidad) {
                elegidos.add(item);
                peso += item.getPeso();
                valor += item.getValor();
            }
        }
        return new Resultado(elegidos, valor, peso, "GREEDY");
    }

    /**
     * PROGRAMACION DINAMICA: mochila 0/1 con tabla dp.
     *
     * Requisitos:
     *   - construir la tabla dp[i][j] de forma iterativa (bottom-up)
     *   - RECUPERAR EL CAMINO: devolver que items componen la solucion optima,
     *     no solo el valor maximo. Eso se hace recorriendo la tabla hacia atras
     *   - los pesos deben ser enteros o discretizarse para indexar la tabla
     *
     * Complejidad esperada: O(n * W) en tiempo y en espacio.
     *
     * Para el coloquio: explicar por que O(n*W) es pseudopolinomica.
     *
     * dp[i][j] = mejor valor usando solo los primeros i items con capacidad j.
     *   dp[0][j] = 0                                   (sin items no hay valor)
     *   dp[i][j] = dp[i-1][j]                          si el item i no entra
     *   dp[i][j] = max(dp[i-1][j],                     (no tomarlo)
     *                  dp[i-1][j - peso_i] + valor_i)  (tomarlo)
     *
     * Pseudopolinomica: W es un NUMERO de la entrada, no una cantidad de
     * elementos. Escribirlo ocupa log W bits, asi que O(n * W) es exponencial
     * en el tamanio de la entrada: duplicar W duplica el tiempo.
     */
    public Resultado programacionDinamica(List<Item> items, int capacidad) {
        validar(items, capacidad);
        int n = items.size();

        // los pesos indexan columnas de la tabla: tienen que ser enteros
        int[] pesos = new int[n];
        for (int i = 0; i < n; i++) {
            double peso = items.get(i).getPeso();
            if (peso != Math.floor(peso)) {
                throw new IllegalArgumentException("La programacion dinamica necesita pesos enteros: "
                        + items.get(i).getId() + " pesa " + peso);
            }
            pesos[i] = (int) peso;
        }

        double[][] dp = new double[n + 1][capacidad + 1]; // fila 0 en 0: caso base
        for (int i = 1; i <= n; i++) {
            int peso = pesos[i - 1];
            double valor = items.get(i - 1).getValor();
            for (int j = 0; j <= capacidad; j++) {
                dp[i][j] = dp[i - 1][j]; // no tomar el item i
                if (peso <= j && dp[i - 1][j - peso] + valor > dp[i][j]) {
                    dp[i][j] = dp[i - 1][j - peso] + valor; // tomarlo mejora
                }
            }
        }

        // RECUPERAR EL CAMINO: de dp[n][W] hacia atras. Si el valor cambio
        // respecto de la fila anterior, el item i se tomo y se descuenta su peso.
        boolean[] tomado = new boolean[n];
        int j = capacidad;
        for (int i = n; i >= 1; i--) {
            if (dp[i][j] != dp[i - 1][j]) {
                tomado[i - 1] = true;
                j -= pesos[i - 1];
            }
        }

        List<Item> elegidos = new ArrayList<>();
        double pesoTotal = 0;
        for (int i = 0; i < n; i++) {
            if (tomado[i]) {
                elegidos.add(items.get(i));
                pesoTotal += pesos[i];
            }
        }
        return new Resultado(elegidos, dp[n][capacidad], pesoTotal, "PROGRAMACION_DINAMICA");
    }

    private void validar(List<Item> items, double capacidad) {
        if (capacidad < 0) {
            throw new IllegalArgumentException("La capacidad no puede ser negativa: " + capacidad);
        }
        for (Item item : items) {
            if (item.getPeso() < 0) {
                throw new IllegalArgumentException("El item " + item.getId() + " tiene peso negativo");
            }
        }
    }
}
