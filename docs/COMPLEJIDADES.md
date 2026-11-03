# Informe de complejidades

Resumen de los algoritmos del trabajo: técnica, recurrencia cuando corresponde,
complejidad temporal y espacial, y la estructura de datos que la sostiene. La
justificación paso a paso de cada fila está en su ficha de
[`ENDPOINTS.md`](ENDPOINTS.md); acá se junta todo para compararlo.

**Tamaño del dominio** (La Ruta del Temple, ver el README):

| Símbolo | Qué es | Valor |
|---|---|---|
| V | ciudades | 20 |
| E | rutas (no dirigidas) | 36; en la lista de adyacencia son 2E = 72 entradas |
| d | grado máximo (ROM, BRI y ROD) | 5 |
| n | provisiones | 9 |
| W | capacidad de la mochila | 15 kg |

Los logaritmos son en base 2: log V ≈ 4,32, log E ≈ 5,17, log n ≈ 3,17.

---

## Tabla general

| Algoritmo | Técnica | Recurrencia | Tiempo | Espacio | Estructura que la sostiene | En el dominio | Por qué |
|---|---|---|---|---|---|---|---|
| [DFS](ENDPOINTS.md#get-apigrafodfs) | Recorrido en profundidad | — (una llamada por ciudad) | O(V + E) | O(V) | Lista de adyacencia + `boolean[]` visitados + pila de recursión | V + 2E = 92 pasos | Cada ciudad se visita una vez y cada lista de vecinos se recorre una vez |
| [BFS](ENDPOINTS.md#get-apigrafobfs) | Recorrido en anchura | — | O(V + E) | O(V) | Lista de adyacencia + cola FIFO (`ArrayDeque`) + `boolean[]` | V + 2E = 92 pasos | Se marca al encolar: cada ciudad entra una sola vez a la cola |
| [Dijkstra](ENDPOINTS.md#get-apigrafodijkstra) | Greedy | — | O((V + E) log V) | O(V + E) | `PriorityQueue` (heap binario) + `dist[]` + `predecesor[]` | 20 expansiones y 72 aristas examinadas; cota 56 · 4,32 ≈ 242 | Hasta 2E entradas en el heap, cada `add`/`poll` en O(log V) |
| [Floyd-Warshall](ENDPOINTS.md#get-apigrafofloyd) | Programación dinámica | D(k,i,j) = min(D(k−1,i,j), D(k−1,i,k) + D(k−1,k,j)) | O(V³) | O(V²) | Matriz `double[][]` | V³ = 8.000 iteraciones (medido) | Tres bucles anidados de V con trabajo constante |
| [Prim](ENDPOINTS.md#get-apigrafoprim) | Greedy | — | O(E log V) | O(V + E) | `PriorityQueue` + `clave[]`, `padre[]`, `enArbol[]` | 20 ciudades, 72 aristas examinadas; cota 36 · 4,32 ≈ 156 | Igual que Dijkstra; se eligió heap porque el grafo es ralo |
| [Kruskal](ENDPOINTS.md#get-apigrafokruskal) | Greedy | — | O(E log E) | O(V + E) | MergeSort propio + Union-Find (compresión de caminos y unión por rango) | 36 rutas ordenadas, 29 consultas a Union-Find; cota 36 · 5,17 ≈ 186 | Domina ordenar; cada `find`/`union` es O(α(V)) |
| [QuickSort](ENDPOINTS.md#get-apiseleccionquicksort) | División y conquista | Promedio T(n) = 2T(n/2) + O(n); peor T(n) = T(n−1) + O(n) | O(n log n) promedio, O(n²) peor | O(n) (copia) + O(log n) a O(n) de pila | Lista + partición de Lomuto, pivote del medio | 9 · 3,17 ≈ 29 promedio; 9² = 81 peor | Depende de qué tan pareja parta el pivote |
| [MergeSort](ENDPOINTS.md#get-apiseleccionmergesort) | División y conquista | T(n) = 2T(n/2) + O(n) | O(n log n) siempre | O(n) | Listas auxiliares para la mezcla | 9 · 3,17 ≈ 29 | Parte siempre por la mitad; teorema maestro con a = b^k |
| [Greedy (mochila)](ENDPOINTS.md#get-apiselecciongreedy) | Greedy | — | O(n log n) | O(n) | MergeSort propio + `ArrayList` | ≈ 29 para ordenar + 9 para elegir | Domina el ordenamiento por ratio |
| [Mochila 0/1](ENDPOINTS.md#get-apiselecciondinamica) | Programación dinámica | dp[i][j] = max(dp[i−1][j], dp[i−1][j − pᵢ] + vᵢ) | O(n · W), pseudopolinomial | O(n · W) | Matriz `double[n+1][W+1]` | 10 × 16 = 160 celdas | Cada celda se llena en O(1) mirando la fila anterior |
| [Backtracking](ENDPOINTS.md#get-apigraforutas) | Backtracking con poda | T(m) = m · T(m − 1) + O(1) en un grafo completo (m = ciudades libres) | O(V!) peor caso | O(V) + la salida | Lista de adyacencia + recursión + `boolean[]` visitados + `camino` como pila | 4.970 nodos con poda vs 62.509 sin poda (medido, PAR→JER, 30/10) | Enumera caminos simples; la poda no cambia el peor caso pero achica el árbol |
| [Branch & Bound](ENDPOINTS.md#get-apiseleccionrepartir) | Ramificación y poda | — | _pendiente (hito 11)_ | _pendiente_ | _pendiente_ | _pendiente_ | _pendiente_ |

UCS es opcional y no está implementado, por eso no figura.

---

## Recurrencias

**QuickSort.** Cada partición recorre su rango una vez: O(n) por nivel.
- Caso promedio (pivote que parte en mitades parecidas):
  T(n) = 2T(n/2) + O(n). Hay log n niveles de O(n): **O(n log n)**.
- Peor caso (pivote siempre mínimo o máximo): T(n) = T(n−1) + O(n) =
  n + (n−1) + … + 1 = **O(n²)**. Con el pivote del medio, una lista ya ordenada
  no cae en este caso.

**MergeSort.** T(n) = 2T(n/2) + O(n): dos mitades más una mezcla lineal. Con el
teorema maestro, a = 2, b = 2, k = 1; como a = b^k, T(n) = O(n^k · log n) =
**O(n log n)**, sin peor caso distinto porque la división no depende de los datos.

**Floyd-Warshall.** D(k,i,j) es el costo mínimo de i a j usando solo los
intermedios 0..k. Hay V etapas (una por k) y en cada una se calculan V² celdas
en O(1): **O(V³)**. Por eso k va en el bucle de afuera.

**Mochila 0/1.** dp[i][j] = mejores jornadas con las primeras i provisiones y j
kg. Hay (n + 1)(W + 1) celdas de O(1): **O(n · W)**. Es pseudopolinomial: W es un
número de la entrada, no una cantidad de elementos.

**Backtracking.** Cada llamada extiende la ruta por cada vecino no visitado. En
el peor caso (grafo completo) la ciudad actual tiene m ciudades libres y
T(m) = m · T(m − 1) + O(1), que da O(m!); con m = V − 1 al empezar, **O(V!)**.
Con el tope de saltos s, la profundidad llega a h = s + 1 y el árbol tiene a lo
sumo 1 + d + d² + … + dʰ nodos: con d = 5 y s = 10 son unos 61 millones como
cota, contra 4.970 nodos reales con poda.

---

## Comparaciones

**Floyd contra Dijkstra × V** (todos los pares). Floyd es O(V³) = 8.000
iteraciones; Dijkstra desde cada ciudad es O(V(V + E) log V), y medido son 400
expansiones y 1.440 intentos de relajación en 20 ejecuciones. En un grafo ralo
y sin costos negativos, como este, conviene Dijkstra × V; Floyd conviene en
grafos densos o con aristas negativas sin ciclos negativos. Ver la ficha de
Floyd para la tabla completa de métricas.

**Prim contra Kruskal.** El grafo es ralo (E = 36, lejos de V² = 400), así que
Prim con heap (O(E log V)) y Kruskal (O(E log E)) andan parecido. En un grafo
denso conviene Prim con arreglos, O(V²), porque Kruskal pagaría O(V² log V) por
ordenar.

**Greedy contra programación dinámica.** Greedy es O(n log n) pero no da el
óptimo en la mochila 0/1: con 15 kg junta 19 jornadas contra las 22 de PD. PD
es O(n · W): con W chico (15) son solo 160 celdas, pero crece con el número W,
no con la cantidad de provisiones.

**Backtracking con y sin poda.** Los dos son exponenciales en el peor caso. En
PAR → JER con 30 jornadas y 10 etapas, la poda explora 4.970 nodos contra
62.509 (unas 12,6 veces menos) y devuelve las mismas 86 rutas.

**QuickSort contra MergeSort.** Los dos son O(n log n) en promedio. MergeSort
garantiza O(n log n) siempre y es estable, por eso lo usan Kruskal y el greedy;
QuickSort puede caer en O(n²) y no es estable.
