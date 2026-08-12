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
