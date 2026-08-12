# Guía del TPO — Programación III

Cómo encarar el trabajo sin perder tiempo ni puntos.

---

## 1. Elegir el dominio

El dominio es libre, pero **no todos los dominios sirven**. El error más caro
del trabajo es elegir uno donde la mitad de los algoritmos no se puedan aplicar
y darse cuenta en octubre.

### La prueba de los 30 segundos

Antes de escribir una línea, respondan estas tres preguntas sobre su dominio:

1. **¿Qué es una arista, y qué número tiene encima?**
   Si no pueden completar la frase *"la conexión entre X e Y cuesta N ___"*,
   el dominio no sirve. Sin peso numérico no hay Dijkstra, ni Prim, ni Kruskal:
   son 3 de los 10 puntos.

2. **¿Tiene sentido preguntar "cuál es el camino más barato de A a B"?**
   Si la respuesta es "no, porque nunca se pasa por nodos intermedios",
   el grafo es bipartito y Dijkstra queda de adorno.

3. **¿Hay algo que seleccionar bajo una restricción de capacidad?**
   Hace falta para greedy, mochila 0/1 y Branch & Bound: otros 3 puntos.

### Dominios que funcionan bien

| Dominio | Nodo | Costo de la arista | Item |
|---|---|---|---|
| Red logística | depósito, punto de entrega | km o minutos entre puntos | paquete (peso, valor) |
| Transporte urbano | estación | tiempo de viaje del tramo | pasajero (equipaje, prioridad) |
| Red eléctrica | subestación | costo de tendido | carga a abastecer |
| Red de datos | servidor, router | latencia del enlace | job (CPU, ganancia) |
| Turismo | ciudad | precio del pasaje | actividad (horas, puntaje) |

### Dominios que se ven bien y no funcionan

- **Jugadores y equipos**: la relación es jugador→equipo, bipartita y sin peso.
  No hay camino de un jugador a otro. Prim y Kruskal quedan artificiales.
- **Alumnos y materias**, **productos y categorías**, **usuarios y roles**:
  mismo problema, son relaciones de pertenencia, no de conexión.
- **Red social sin peso**: sirve para BFS y DFS, pero Dijkstra necesita que
  las amistades tengan un número encima, y "cuánto se quieren" no convence.

> **Regla práctica:** si su grafo se dibuja con dos columnas de nodos y flechas
> de izquierda a derecha, es bipartito y les va a faltar la mitad del trabajo.

---

## 2. Orden de implementación

No empiecen por lo que les parece más divertido. Este orden va de mayor a menor
retorno por hora invertida, y cada paso reutiliza el anterior.

| Orden | Qué | Puntos | Por qué acá |
|---|---|---|---|
| 1 | **BFS y DFS** | 2 | Los más simples y los que más valen. DFS además es la base del backtracking. |
| 2 | **Dijkstra** | parte de 3 | Reutiliza la lista de adyacencia que ya recorrieron con BFS. |
| 3 | **Prim y Kruskal** | resto de 3 | Comparten estructura con Dijkstra. Ojo: grafo NO dirigido. |
| 4 | **QuickSort y MergeSort** | 1 | Independientes de todo lo demás. Se pueden hacer en paralelo por otro integrante. |
| 5 | **Greedy** | 1 | Necesita el ordenamiento del paso 4. |
| 6 | **Mochila 0/1 con PD** | 1 | Resuelve el mismo problema que el 5. La comparación es lo que se evalúa. |
| 7 | **Backtracking** | 1 | Reutiliza el DFS del paso 1. |
| 8 | **Branch & Bound** | 1 | Es el paso 7 más una cota. Hacerlo antes que el 7 no tiene sentido. |

Con los pasos 1 a 3 ya tienen **5 puntos**: la mitad del trabajo grupal
aprobado, y es la parte más mecánica.

---

## 3. Cómo se implementa un algoritmo acá

Los endpoints ya están cableados. Para que uno funcione hay que tocar
**un solo archivo**: el del paquete `algorithm`.

```java
// ANTES — src/main/java/uade/prog3/tpo/algorithm/Recorridos.java
public List<String> bfs(Grafo grafo, String origenId) {
    throw new PendienteDeImplementar("BFS");
}

// DESPUÉS
public List<String> bfs(Grafo grafo, String origenId) {
    int origen = grafo.indiceDe(origenId);
    boolean[] visitado = new boolean[grafo.cantidadVertices()];
    List<String> orden = new ArrayList<>();
    Deque<Integer> cola = new ArrayDeque<>();

    visitado[origen] = true;      // marcar AL ENCOLAR, no al desencolar
    cola.add(origen);

    while (!cola.isEmpty()) {
        int u = cola.poll();
        orden.add(grafo.idDe(u));
        for (Grafo.Arista a : grafo.vecinos(u)) {
            if (!visitado[a.destino()]) {
                visitado[a.destino()] = true;
                cola.add(a.destino());
            }
        }
    }
    return orden;
}
```

Eso es todo: el controller, la carga del grafo y el manejo de errores ya están.

### Lo que NO hay que hacer

- **No poner algoritmos en el controller.** El controller carga el grafo y
  delega. Si abren un `for` dentro de un `@GetMapping`, algo está mal.
- **No consultar la base desde el algoritmo.** El grafo se carga una vez con
  `grafoService.cargar()`. Si su Dijkstra hace un query por cada nodo, la
  complejidad que van a defender en el coloquio no es la del algoritmo.
- **No usar librerías.** `Collections.sort`, JGraphT o similares invalidan el
  punto. Se evalúa que implementen el algoritmo, no que lo llamen.

---

## 4. Errores que aparecen todos los cuatrimestres

| Error | Síntoma | Arreglo |
|---|---|---|
| BFS marca visitado al desencolar | funciona pero un nodo entra varias veces a la cola | marcar al encolar |
| Dijkstra sin relajación | devuelve el camino directo aunque haya uno más barato | comparar `dist[u] + peso` contra `dist[v]` |
| Prim sobre grafo dirigido | el MST no incluye todos los vértices | cargar con `cargar(false)` |
| Kruskal sin Union-Find | genera ciclos | implementar `find` y `union` |
| Backtracking sin deshacer | resultados con vértices repetidos | desmarcar el visitado al volver |
| B&B con cota pesimista | devuelve una solución peor que la óptima, sin avisar | la cota debe ser optimista |
| Floyd con el bucle `k` adentro | matriz que parece bien pero está mal | `k` va **siempre** en el bucle externo |
| Greedy presentado como óptimo | pierde puntos en el coloquio | greedy en mochila 0/1 no es óptimo, y hay que decirlo |

### Cómo verificar sin depender de la suerte

Sobre el grafo de ejemplo del README, estos resultados están calculados a mano:

- `dijkstra(A, C)` = **7** por el camino A→B→C, no 9 por el directo
- `dijkstra(A, H)` = **20** por A→B→C→D→F→H (4+3+2+8+3)
- `prim(A)` y `kruskal()` deben dar **costo total 27**, aunque el conjunto de
  aristas pueda diferir si hay pesos repetidos
- el MST debe tener exactamente **7 aristas** (V−1 con V=8): C-D(2) B-C(3)
  F-H(3) A-B(4) G-H(4) D-E(5) E-F(6)

Si su implementación no da estos números, hay un bug. No lo dejen para el final.

---

## 5. Documentar los endpoints

Sin documentación, el punto **no se asigna** aunque el código funcione.
Creen `docs/ENDPOINTS.md` con este formato para cada algoritmo:

```markdown
### GET /api/grafo/dijkstra

Camino de costo mínimo entre dos nodos.

**Parámetros:** `origen` (id), `destino` (id)

**Ejemplo:**  `GET /api/grafo/dijkstra?origen=A&destino=H`

**Respuesta:**
{ "vertices": ["A","B","C","D","F","H"], "costoTotal": 20.0 }

**Complejidad:** O((V+E) log V) — cola de prioridad con heap binario.
**Estructura usada:** PriorityQueue + arreglo de predecesores.
```

Las tres últimas líneas son las que se miran en la corrección: qué complejidad
tiene, por qué, y con qué estructura.

---

## 6. Preparar el coloquio

El coloquio individual vale **6 de los 10 puntos individuales**, más que todo
lo demás junto. Y se pregunta sobre **cualquier** algoritmo del trabajo, no
solo sobre el que programó cada uno.

Antes de la defensa, cada integrante debería poder:

- explicar en voz alta, sin mirar el código, cómo funciona cada algoritmo
- decir su complejidad **y justificarla**, no recitarla
- comparar dos algoritmos del trabajo y decir cuándo conviene cada uno
- mostrar un caso donde greedy no da el óptimo, con números concretos
- señalar en su código dónde está la poda, la relajación o el retroceso

Preguntas típicas: *"Explicá cómo funciona DFS"*, *"Compará BFS con Dijkstra
en este caso"*, *"¿Por qué acá usaron programación dinámica y no greedy?"*,
*"¿Qué pasa si el grafo tiene una arista de peso negativo?"*.

**Repartirse los algoritmos para programar está bien. Repartirse los algoritmos
para estudiar, no.**
