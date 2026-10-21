# Documentación de endpoints

> Plantilla. Cada grupo la completa con SUS endpoints, sus ejemplos reales
> y su análisis de complejidad. Un endpoint sin esta ficha no suma puntos.

---

### GET /api/grafo/dfs

Recorrido en profundidad desde un nodo.

**Parámetros:** `origen` (id del nodo de partida)

**Ejemplo:** `GET /api/grafo/dfs?origen=A`

**Respuesta:**
```json
["A", "B", "C", "D", "E", "F", "H", "G"]
```

**Complejidad:** O(V + E) — cada vértice se visita una vez y cada arista se
recorre una vez, con lista de adyacencia.

**Estructura usada:** recursión (pila de llamadas) + arreglo de visitados.

---

### GET /api/grafo/bfs

_(completar)_

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
  {"id":"I1","nombre":"Item 1","peso":5.0,"valor":10.0,"nodoId":"A"},
  {"id":"I3","nombre":"Item 3","peso":6.0,"valor":30.0,"nodoId":"C"},
  {"id":"I5","nombre":"Item 5","peso":7.0,"valor":55.0,"nodoId":"E"},
  {"id":"I2","nombre":"Item 2","peso":4.0,"valor":40.0,"nodoId":"B"},
  {"id":"I4","nombre":"Item 4","peso":3.0,"valor":50.0,"nodoId":"D"}
]
```
Ratios: 2, 5, 7.9, 10, 16.7.

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

**Respuesta:**
```json
[
  {"id":"I4","nombre":"Item 4","peso":3.0,"valor":50.0,"nodoId":"D"},
  {"id":"I2","nombre":"Item 2","peso":4.0,"valor":40.0,"nodoId":"B"},
  {"id":"I1","nombre":"Item 1","peso":5.0,"valor":10.0,"nodoId":"A"},
  {"id":"I3","nombre":"Item 3","peso":6.0,"valor":30.0,"nodoId":"C"},
  {"id":"I5","nombre":"Item 5","peso":7.0,"valor":55.0,"nodoId":"E"}
]
```

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
