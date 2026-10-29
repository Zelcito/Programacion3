# Documentación de endpoints

> Plantilla. Cada grupo la completa con SUS endpoints, sus ejemplos reales
> y su análisis de complejidad. Un endpoint sin esta ficha no suma puntos.

---

> Todos los ejemplos usan el dominio cargado en Neo4j: La Ruta del Temple
> (20 ciudades, 36 rutas, 9 provisiones). Ver el README.

---

### GET /api/grafo/dfs

Recorrido en profundidad desde una ciudad: devuelve las ciudades a las que se
puede llegar, en el orden en que el recorrido las visita. Desde cada ciudad
baja por la primera ruta a una ciudad sin visitar, y retrocede recién cuando
no le quedan rutas nuevas.

**Parámetros:** `origen` (id de la ciudad de partida, por ejemplo `PAR`).
Una ciudad que no existe devuelve 400.

**Ejemplo:** `GET /api/grafo/dfs?origen=PAR`

**Respuesta:**
```json
["PAR","TRO","LYO","MRS","BCN","PAL","ROM","GEN","VEN","BRI",
 "TES","ATE","CRE","ROD","CON","ICO","ANT","CHI","TRI","JER"]
```

El orden depende del orden de los vecinos en la lista de adyacencia. El
ejemplo es el que sale con las rutas en el orden de la tabla del README, que es
el que verifica `RecorridosTest`. Neo4j no garantiza en qué orden devuelve las
relaciones de cada nodo, así que el endpoint puede devolver otro orden igual de
válido, por ejemplo:
`["PAR","TRO","LYO","MRS","GEN","VEN","BRI","ROM","PAL","BCN","CRE","ATE","TES","CON","ROD","ICO","ANT","CHI","TRI","JER"]`.
Lo que no cambia es que visita las 20 ciudades, cada una una sola vez.

**Complejidad:** O(V + E).
- Cada ciudad se marca como visitada antes de explorarla y nunca se vuelve a
  entrar: hay V llamadas recursivas.
- Cada llamada recorre la lista de vecinos de su ciudad una vez. Sumando todas
  las listas, en un grafo no dirigido son 2E aristas: O(E).
- Con matriz de adyacencia sería O(V²), porque para cada ciudad habría que
  revisar las V columnas.

**Espacio:** O(V) para el arreglo de visitados y la pila de recursión, que en el
peor caso tiene V llamadas (desde París llega a profundidad 20).

**Estructura usada:** lista de adyacencia + arreglo `boolean[]` de visitados +
recursión (la pila de llamadas hace de pila).

---

### GET /api/grafo/bfs

Recorrido en anchura desde una ciudad: primero las ciudades a una ruta de
distancia, después las que están a dos, y así. El orden de visita es por
cantidad de etapas desde el origen.

**Parámetros:** `origen` (id de la ciudad de partida). Una ciudad que no existe
devuelve 400.

**Ejemplo:** `GET /api/grafo/bfs?origen=PAR`

**Respuesta:**
```json
["PAR","TRO","LYO","MRS","GEN","BCN","ROM","VEN","PAL","BRI",
 "CRE","TES","ATE","ROD","CHI","CON","ICO","TRI","ANT","JER"]
```

Dentro de un mismo nivel, el orden puede variar según en qué orden devuelva
Neo4j las rutas de cada ciudad. Lo que no cambia es qué ciudades hay en cada
nivel.

Por niveles (etapas desde París):

| Etapas | Ciudades |
|---|---|
| 0 | PAR |
| 1 | TRO, LYO |
| 2 | MRS, GEN |
| 3 | BCN, ROM, VEN |
| 4 | PAL, BRI |
| 5 | CRE, TES, ATE |
| 6 | ROD, CHI, CON |
| 7 | ICO, TRI, ANT, JER |

**BFS no da el camino más barato.** Jerusalén está a 7 etapas, por ejemplo
PAR → LYO → MRS → BCN → PAL → CRE → CHI → JER, que cuesta 29 jornadas. En cambio
PAR → LYO → MRS → ROM → BRI → ATE → ROD → CHI → JER tiene una etapa más y cuesta
26 (ver el README). BFS minimiza la
cantidad de aristas, no la suma de sus costos: para eso está Dijkstra.

**Complejidad:** O(V + E).
- Cada ciudad se marca como visitada al encolarla, así que entra a la cola una
  sola vez: V operaciones de encolar y desencolar, O(1) cada una.
- Al desencolar una ciudad se recorre su lista de vecinos una vez: 2E en total
  en un grafo no dirigido.
- Si se marcara al desencolar, una ciudad podría entrar varias veces a la cola
  (por ejemplo ROM, que es vecina de MRS y de GEN, las dos del nivel anterior).

**Espacio:** O(V) para el arreglo de visitados y la cola.

**Estructura usada:** lista de adyacencia + cola FIFO (`ArrayDeque`) + arreglo
`boolean[]` de visitados.

---

### GET /api/grafo/dijkstra

Ruta de menos jornadas entre dos ciudades. Devuelve las ciudades del camino, en
orden, y el total de jornadas.

**Parámetros:** `origen` y `destino` (ids de ciudad, por ejemplo `PAR` y `JER`).
Una ciudad que no existe devuelve 400. Una ruta de costo negativo devuelve 400
con la ruta que la causa. Si no hay ningún camino entre las dos ciudades, 422.
Origen igual a destino devuelve `{"vertices":["PAR"],"costoTotal":0.0}`.

**Ejemplo:** `GET /api/grafo/dijkstra?origen=PAR&destino=JER`

**Respuesta:**
```json
{"vertices":["PAR","LYO","MRS","ROM","BRI","ATE","ROD","CHI","JER"],"costoTotal":26.0}
```
3 + 2 + 4 + 4 + 4 + 3 + 3 + 3 = 26 jornadas. Es el único camino de 26, así que
el resultado no depende del orden en que Neo4j devuelva las rutas.

**Cálculo a mano** desde París: cada ciudad con su distancia definitiva y la
ciudad desde la que llega, en el orden en que Dijkstra la saca de la cola.

| # | Ciudad | Jornadas | Desde | | # | Ciudad | Jornadas | Desde |
|---|---|---|---|---|---|---|---|---|
| 1 | PAR | 0 | — | | 11 | ATE | 17 | BRI |
| 2 | TRO | 2 | PAR | | 12 | TES | 18 | BRI |
| 3 | LYO | 3 | PAR | | 13 | CRE | 19 | PAL o ATE (empate: 12 + 7 = 17 + 2) |
| 4 | MRS | 5 | LYO | | 14 | ROD | 20 | ATE |
| 5 | GEN | 7 | LYO | | 15 | CON | 21 | ATE |
| 6 | BCN | 8 | MRS | | 16 | CHI | 23 | ROD |
| 7 | ROM | 9 | MRS | | 17 | ICO | 24 | ROD |
| 8 | VEN | 10 | GEN | | 18 | TRI | 25 | CHI |
| 9 | PAL | 12 | ROM | | 19 | ANT | 26 | CHI |
| 10 | BRI | 13 | ROM | | 20 | JER | 26 | CHI |

Siguiendo la columna "Desde" hacia atrás desde JER sale el camino de la respuesta.
`CaminosMinimosTest` verifica además PAR→ROM = 9, PAR→ATE = 17, PAR→CON = 21,
PAR→CHI = 23 y ROM→JER = 17, y sobre el grafo A..H de `GUIA-TPO.md`, A→C = 7
(no 9 por la ruta directa) y A→H = 20.

**Dijkstra contra BFS:** BFS llega a Jerusalén en 7 etapas, por ejemplo
PAR → LYO → MRS → BCN → PAL → CRE → CHI → JER, que cuesta 29 jornadas. Dijkstra
usa 8 etapas y 26 jornadas. BFS minimiza la cantidad de rutas; Dijkstra, la suma
de sus costos.

**Por qué es greedy:** en cada paso saca de la cola la ciudad sin fijar más
cercana al origen y deja su distancia fija para siempre, sin volver a revisarla.
Es correcto porque, con costos mayores o iguales a 0, cualquier otro camino hasta
esa ciudad tiene que pasar por alguna ciudad que sigue en la cola, que ya está
igual o más lejos, y sumarle rutas no lo achica.

**Por qué los costos negativos lo rompen:** esa garantía deja de valer. Con
A→B = 2, A→C = 3 y C→B = −2, Dijkstra fija B en 2 antes de mirar C, pero
A→C→B cuesta 1. En un grafo no dirigido es peor: una ruta negativa recorrida de
ida y vuelta es un ciclo negativo, y entonces no existe camino mínimo. Por eso se
valida antes de empezar.

**Complejidad:** O((V + E) log V).
- Validar que no haya costos negativos e inicializar `dist[]` y `predecesor[]`: O(V + E).
- Cada relajación que mejora una distancia encola una entrada nueva (inserción
  perezosa: en vez de bajarle la prioridad a la que ya estaba). Hay a lo sumo
  una por arista dirigida, 2E en un grafo no dirigido. Cada `add` y `poll` del
  heap cuesta O(log E), y como E ≤ V², log E ≤ 2 log V: O(log V).
- Cada ciudad se procesa una sola vez, porque las entradas viejas se descartan
  al salir (`if (actual.distancia() > dist[u]) continue`). Al procesarla se
  recorren sus vecinos: 2E en total.
- Reconstruir el camino siguiendo `predecesor[]`: O(V).
- Con búsqueda lineal del mínimo en lugar de heap sería O(V²).

**Espacio:** O(V + E): `dist[]` y `predecesor[]` de V posiciones, y la cola,
que puede llegar a tener una entrada por arista.

**Estructura usada:** lista de adyacencia + `PriorityQueue` (heap binario) +
arreglos `dist[]` y `predecesor[]`. La relajación está en `CaminosMinimos.dijkstra`,
marcada `// RELAJACION`.

---

### GET /api/grafo/prim

Árbol de recubrimiento mínimo: las rutas que conectan las 20 ciudades con el
menor total de jornadas. En el dominio, son las rutas que el Temple tendría que
custodiar para que una encomienda pueda llegar a cualquier ciudad. Prim hace
crecer el árbol desde una ciudad y en cada paso suma la ruta más barata que sale
del árbol hacia una ciudad que todavía no está.

**Parámetros:** `origen` (ciudad desde la que crece el árbol). El costo total es
el mismo desde cualquier ciudad, y `ArbolRecubrimientoTest` lo verifica para las
20. Una ciudad que no existe devuelve 400. Si el grafo no es conexo, 422.

**Ejemplo:** `GET /api/grafo/prim?origen=PAR`

**Respuesta** (con las rutas en el orden de la tabla del README, el que usan los
tests; las aristas salen en el orden en que entran al árbol):
```json
{"aristas":[
  {"origen":"PAR","destino":"TRO","costo":2.0},{"origen":"TRO","destino":"LYO","costo":2.0},
  {"origen":"LYO","destino":"MRS","costo":2.0},{"origen":"MRS","destino":"GEN","costo":3.0},
  {"origen":"MRS","destino":"BCN","costo":3.0},{"origen":"GEN","destino":"ROM","costo":3.0},
  {"origen":"GEN","destino":"VEN","costo":3.0},{"origen":"ROM","destino":"PAL","costo":3.0},
  {"origen":"PAL","destino":"BRI","costo":3.0},{"origen":"BRI","destino":"ATE","costo":4.0},
  {"origen":"ATE","destino":"CRE","costo":2.0},{"origen":"CRE","destino":"ROD","costo":2.0},
  {"origen":"ROD","destino":"CHI","costo":3.0},{"origen":"CHI","destino":"TRI","costo":2.0},
  {"origen":"TRI","destino":"ANT","costo":2.0},{"origen":"ATE","destino":"TES","costo":3.0},
  {"origen":"CHI","destino":"JER","costo":3.0},{"origen":"ATE","destino":"CON","costo":4.0},
  {"origen":"ROD","destino":"ICO","costo":4.0}],
 "costoTotal":53.0}
```
19 aristas (V − 1 con V = 20): siete de 2, nueve de 3 y tres de 4, o sea
14 + 27 + 12 = 53 jornadas.

Como hay costos repetidos, el grafo tiene varios árboles mínimos. Si Neo4j
devuelve las rutas en otro orden, el conjunto puede cambiar, pero el costo total
es siempre 53, con siete rutas de 2, nueve de 3 y tres de 4. Sobre el grafo A..H
de `GUIA-TPO.md`, Prim y Kruskal dan 27 con 7 aristas.

**Implementación elegida: con heap**, O(E log V), y no con arreglos, O(V²). El
grafo es ralo: E = 36, lejos de V² = 400. Con arreglos, cada uno de los 20 pasos
recorre las 20 ciudades buscando la clave mínima, aunque cada ciudad tenga solo
entre 2 y 5 rutas. La versión con arreglos conviene en grafos densos (E cerca de
V²), donde E log V pasa a ser más que V².

**Complejidad:** O(E log V).
- Cada ciudad entra al árbol una sola vez (`enArbol[]`); las entradas viejas de
  la cola se descartan al salir.
- Al entrar una ciudad se recorren sus vecinos: 2E en total. Cada vecino al que
  le baja la `clave[]` se encola: a lo sumo 2E entradas, con `add` y `poll` en
  O(log V) (el mismo argumento que en Dijkstra).
- Inicialización: O(V). Total O(V + E log V), que es O(E log V) en un grafo
  conexo porque E ≥ V − 1.

**Espacio:** O(V + E): `clave[]`, `padre[]` y `enArbol[]` de V posiciones, y la cola.

**Estructura usada:** lista de adyacencia + `PriorityQueue` (heap binario) +
arreglos `clave[]` (ruta más barata que une cada ciudad con el árbol), `padre[]`
y `enArbol[]`.

---

### GET /api/grafo/kruskal

El mismo árbol de recubrimiento mínimo, armado de otra forma: ordena las 36 rutas
de menor a mayor costo y las va agregando, salteando las que cerrarían un ciclo
con las ya elegidas.

**Parámetros:** ninguno. Si el grafo no es conexo, 422.

**Ejemplo:** `GET /api/grafo/kruskal`

**Respuesta** (con las rutas en el orden de la tabla del README):
```json
{"aristas":[
  {"origen":"PAR","destino":"TRO","costo":2.0},{"origen":"TRO","destino":"LYO","costo":2.0},
  {"origen":"LYO","destino":"MRS","costo":2.0},{"origen":"ATE","destino":"CRE","costo":2.0},
  {"origen":"CRE","destino":"ROD","costo":2.0},{"origen":"CHI","destino":"TRI","costo":2.0},
  {"origen":"ANT","destino":"TRI","costo":2.0},{"origen":"BCN","destino":"MRS","costo":3.0},
  {"origen":"MRS","destino":"GEN","costo":3.0},{"origen":"GEN","destino":"VEN","costo":3.0},
  {"origen":"GEN","destino":"ROM","costo":3.0},{"origen":"ROM","destino":"PAL","costo":3.0},
  {"origen":"PAL","destino":"BRI","costo":3.0},{"origen":"TES","destino":"ATE","costo":3.0},
  {"origen":"ROD","destino":"CHI","costo":3.0},{"origen":"CHI","destino":"JER","costo":3.0},
  {"origen":"BRI","destino":"ATE","costo":4.0},{"origen":"TES","destino":"CON","costo":4.0},
  {"origen":"CON","destino":"ICO","costo":4.0}],
 "costoTotal":53.0}
```
Cuesta 53 con 19 aristas, igual que Prim, pero el árbol no es el mismo: Prim
toma ATE-CON y ROD-ICO donde Kruskal toma TES-CON y CON-ICO. Cada par cuesta lo
mismo (4 y 4): son empates, y los dos árboles son mínimos.

**Cálculo a mano.** Las rutas van ordenadas por costo y, a igual costo, en el
orden en que las junta el código: ciudad por ciudad según la tabla de ciudades
del README y, dentro de cada ciudad, según la tabla de rutas. Por eso CHI-JER
aparece antes que TRI-JER.

| Costo | Se agregan | Se descartan porque cerrarían un ciclo |
|---|---|---|
| 2 | PAR-TRO, TRO-LYO, LYO-MRS, ATE-CRE, CRE-ROD, CHI-TRI, ANT-TRI | — |
| 3 | BCN-MRS, MRS-GEN, GEN-VEN, GEN-ROM, ROM-PAL, PAL-BRI, TES-ATE, ROD-CHI, CHI-JER | PAR-LYO (por PAR-TRO-LYO), VEN-ROM (VEN-GEN-ROM), ATE-ROD (ATE-CRE-ROD), CHI-ANT (CHI-TRI-ANT), TRI-JER (TRI-CHI-JER) |
| 4 | BRI-ATE, TES-CON, CON-ICO | LYO-GEN (LYO-MRS-GEN), MRS-ROM (MRS-GEN-ROM), VEN-BRI (VEN-GEN-ROM-PAL-BRI), ROM-BRI (ROM-PAL-BRI), ATE-CON (ATE-TES-CON) |

Con CON-ICO se llega a 19 aristas y el algoritmo termina: las 7 rutas que
quedan (CON-ROD, ICO-ANT e ICO-ROD de 4, BRI-TES y CRE-CHI de 5, BCN-PAL de 6 y
PAL-CRE de 7) ya no se miran. 19 agregadas + 10 descartadas + 7 sin mirar = 36.

**Ordenamiento de las rutas:** con el MergeSort propio del hito 2
(`Ordenamiento.mergeSort`), que para esto pasó a ser genérico (antes solo
aceptaba `List<Item>`). No se usa `Collections.sort`. MergeSort es O(E log E)
siempre, mientras que QuickSort puede caer en O(E²), y es estable: a igual costo,
las rutas quedan en el orden en que se leyeron, así que para un mismo grafo el
resultado es siempre el mismo.

Cada ruta aparece dos veces en la lista de adyacencia, una por sentido: se toma
solo la de `u < v`.

**Union-Find:** propio (clase interna `UnionFind`). Cada componente es un árbol
cuya raíz la representa, y una ruta u-v cierra un ciclo si `find(u) == find(v)`,
es decir, si sus dos ciudades ya están en la misma componente. `find` hace
compresión de caminos (cada ciudad recorrida queda colgada directo de la raíz) y
`union` cuelga el árbol de menor rango del de mayor rango. Con las dos cosas,
cada operación cuesta O(α(V)) amortizado, donde α es la inversa de Ackermann:
prácticamente constante.

**Complejidad:** O(E log E), que es O(E log V) porque E ≤ V².
- Juntar las rutas: O(V + E).
- Ordenarlas con MergeSort: O(E log E). Es el paso que domina.
- Recorrerlas consultando Union-Find: O(E · α(V)).

**Espacio:** O(V + E): la lista de rutas, su copia ordenada (más las listas
auxiliares de MergeSort) y los arreglos `padre[]` y `rango[]` de Union-Find.

**Estructura usada:** lista de rutas ordenada con MergeSort + Union-Find con
compresión de caminos y unión por rango.

**¿Prim o Kruskal?** Depende de la densidad del grafo. Kruskal paga O(E log E)
por ordenar todas las rutas. En un grafo ralo eso es poco, pero en uno denso (E
cerca de V²) son O(V² log V), y ahí conviene Prim con arreglos, que es O(V²). En
grafos ralos, Prim con heap y Kruskal andan parecido. La Ruta del Temple es rala
(E = 36, V = 20, V² = 400), así que sirve cualquiera de los dos; Kruskal tiene la
ventaja de que no necesita una ciudad de partida.

---

### GET /api/seleccion/quicksort

Ordena los items de menor a mayor según un criterio. Devuelve una lista nueva:
la que viene de la base no se modifica.

**Parámetros:** `criterio` = `valor`, `peso` o `ratio` (valor/peso). Opcional,
por defecto `ratio`. Cualquier otro valor devuelve 400.

**Ejemplo:** `GET /api/seleccion/quicksort?criterio=ratio`

**Respuesta:**
```json
[
  {"id":"REL","nombre":"Reliquia santa","peso":4.0,"valor":2.0,"nodoId":null},
  {"id":"ESP","nombre":"Espada","peso":5.0,"valor":3.0,"nodoId":null},
  {"id":"UNG","nombre":"Ungüentos","peso":3.0,"valor":2.0,"nodoId":null},
  {"id":"PAN","nombre":"Pan de centeno","peso":2.0,"valor":2.0,"nodoId":null},
  {"id":"SAL","nombre":"Salterio","peso":2.0,"valor":2.0,"nodoId":null},
  {"id":"QUE","nombre":"Queso curado","peso":4.0,"valor":5.0,"nodoId":null},
  {"id":"VIN","nombre":"Vino aguado","peso":8.0,"valor":10.0,"nodoId":null},
  {"id":"CAR","nombre":"Carne salada","peso":3.0,"valor":6.0,"nodoId":null},
  {"id":"ODR","nombre":"Odre de agua","peso":2.0,"valor":4.0,"nodoId":null}
]
```
Ratios (jornadas por kg): 0.5, 0.6, 0.67, 1, 1, 1.25, 1.25, 2, 2. QuickSort no es
estable: entre provisiones con el mismo ratio (PAN y SAL, QUE y VIN, CAR y ODR)
el orden puede cambiar según cómo lleguen de la base.

**Complejidad:** O(n log n) promedio, O(n²) peor caso.
- Cada partición recorre su rango una vez: O(n) por nivel de recursión.
- Si el pivote parte el rango en dos mitades parecidas, hay log n niveles:
  T(n) = 2T(n/2) + O(n) = O(n log n).
- Si el pivote es siempre el mínimo o el máximo del rango, un lado queda vacío
  y el otro con n−1 elementos: T(n) = T(n−1) + O(n) = O(n²).

**Pivote:** el elemento del medio del rango. Con el último como pivote, una lista
ya ordenada cae siempre en el peor caso; con el del medio, esa misma lista se
parte en mitades parejas. El peor caso sigue siendo posible, pero con una
entrada armada a propósito.

**Estructura usada:** partición de Lomuto in-place sobre una copia de la lista
(O(n) de espacio) y recursión (O(log n) de pila en promedio, O(n) en el peor caso).

---

### GET /api/seleccion/mergesort

Ordena los items de menor a mayor según un criterio. Es estable: si dos items
empatan, quedan en el mismo orden en que venían.

**Parámetros:** `criterio` = `valor`, `peso` o `ratio`. Opcional, por defecto `ratio`.

**Ejemplo:** `GET /api/seleccion/mergesort?criterio=peso`

**Respuesta** (con las provisiones en el orden de la tabla del README):
```json
[
  {"id":"PAN","nombre":"Pan de centeno","peso":2.0,"valor":2.0,"nodoId":null},
  {"id":"ODR","nombre":"Odre de agua","peso":2.0,"valor":4.0,"nodoId":null},
  {"id":"SAL","nombre":"Salterio","peso":2.0,"valor":2.0,"nodoId":null},
  {"id":"CAR","nombre":"Carne salada","peso":3.0,"valor":6.0,"nodoId":null},
  {"id":"UNG","nombre":"Ungüentos","peso":3.0,"valor":2.0,"nodoId":null},
  {"id":"QUE","nombre":"Queso curado","peso":4.0,"valor":5.0,"nodoId":null},
  {"id":"REL","nombre":"Reliquia santa","peso":4.0,"valor":2.0,"nodoId":null},
  {"id":"ESP","nombre":"Espada","peso":5.0,"valor":3.0,"nodoId":null},
  {"id":"VIN","nombre":"Vino aguado","peso":8.0,"valor":10.0,"nodoId":null}
]
```
Las de 2 kg (PAN, ODR, SAL) salen en el mismo orden en que entraron: es estable.

**Complejidad:** O(n log n) en todos los casos.
- Recurrencia: T(n) = 2T(n/2) + O(n). Dos subproblemas de la mitad de tamaño,
  más la mezcla, que es lineal.
- Regla práctica: a = 2, b = 2, k = 1. Como a = b^k, T(n) = O(n^k · log n) = O(n log n).
- No tiene peor caso distinto: la división siempre es por la mitad, sin
  importar los datos.

**Estabilidad:** en la mezcla, ante un empate se toma primero el elemento de la
mitad izquierda (`compare <= 0`).

**Estructura usada:** listas auxiliares para la mezcla (O(n) de espacio extra)
y recursión (O(log n) de pila).

---

### GET /api/seleccion/greedy

_(completar — incluir un caso donde greedy NO dé el óptimo)_

---

### GET /api/seleccion/dinamica

_(completar — incluir la tabla dp del ejemplo)_

---

### GET /api/grafo/rutas

_(completar — incluir cantidad de nodos explorados con y sin poda)_

---

### GET /api/seleccion/repartir

_(completar — incluir cuál es la cota y por qué es optimista)_
