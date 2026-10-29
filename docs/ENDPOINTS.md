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

_(completar)_

---

### GET /api/grafo/prim

_(completar)_

---

### GET /api/grafo/kruskal

_(completar)_

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
