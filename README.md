# TPO Programación III — Scaffold 2026

Punto de partida del Trabajo Práctico Obligatorio. **No contiene ningún algoritmo
implementado**: eso es exactamente lo que hay que hacer.

Lo que sí trae resuelto, para que no pierdan tiempo en infraestructura:

- modelo de grafo **ponderado** en Neo4j, listo para Dijkstra, Prim y Kruskal
- carga del grafo de la base a una estructura en memoria (`Grafo`)
- todos los endpoints ya cableados, devolviendo `501 Not Implemented`
- datos de ejemplo que se cargan solos al arrancar
- manejo de errores, configuración por variables de entorno y tests de base

## Arranque rápido

```bash
# 1. Levantar Neo4j (local o Aura) y exportar las credenciales
cp .env.example .env          # completar NEO4J_PASSWORD
export $(cat .env | xargs)

# 2. Compilar y ejecutar
./mvnw spring-boot:run

# 3. Verificar que la base responde
curl "http://localhost:8080/api/grafo/resumen"
# -> {"vertices":8,"aristas":12,"dirigido":true,"ids":["A","B",...]}

# 4. Probar un algoritmo sin implementar
curl "http://localhost:8080/api/grafo/dfs?origen=A"
# -> 501 {"estado":"PENDIENTE","detalle":"Todavia no esta implementado: DFS"}
```

Si el paso 3 responde, la infraestructura está bien y todo lo que queda es
escribir algoritmos.

## Estructura

```
model/          Nodo, Conexion (arista con costo), Item
repository/     acceso a Neo4j — repositorios BLOQUEANTES, sin Mono/Flux
service/        Grafo (lista de adyacencia en memoria) y GrafoService (carga)
algorithm/      >>> ACÁ VA TODO EL TRABAJO <<<  ahora son stubs con TODO
controller/     endpoints ya cableados — NO poner lógica algorítmica acá
seed/           datos de ejemplo
```

## Endpoints

| Método | Endpoint | Algoritmo | Puntos |
|---|---|---|---|
| GET | `/api/grafo/resumen` | — (verificación) | — |
| GET | `/api/grafo/dfs?origen=A` | DFS | 2 (con BFS) |
| GET | `/api/grafo/bfs?origen=A` | BFS | |
| GET | `/api/grafo/dijkstra?origen=A&destino=H` | Dijkstra | 3 (con Prim y Kruskal) |
| GET | `/api/grafo/prim?origen=A` | Prim | |
| GET | `/api/grafo/kruskal` | Kruskal | |
| GET | `/api/grafo/floyd` | Floyd-Warshall | 1 (con mochila 0/1) |
| GET | `/api/grafo/ucs?origen=A&destino=H` | UCS | opcional |
| GET | `/api/seleccion/greedy?capacidad=10` | Greedy | 1 |
| GET | `/api/seleccion/dinamica?capacidad=10` | Mochila 0/1 con PD | 1 |
| GET | `/api/seleccion/quicksort?criterio=ratio` | QuickSort | 1 (con MergeSort) |
| GET | `/api/seleccion/mergesort?criterio=peso` | MergeSort | |
| GET | `/api/grafo/rutas?origen=A&destino=H&costoMaximo=30` | Backtracking | 1 |
| GET | `/api/seleccion/repartir?contenedores=3` | Branch & Bound | 1 |

## El grafo de ejemplo

```
      4        3
  A ------ B ----- C
   \       |     / |  \
    \ 9    | 7  /2 |11 \
     \     |   /   |    \
      ---- C   D --+     E
                |  5    / \
             8  |      /10 \ 6
                F ----+     G
                 \ 3        | 4
                  H --------+
```

Aristas: A-B(4) A-C(9) B-C(3) B-D(7) C-D(2) C-E(11) D-E(5) D-F(8) E-F(6) E-G(10) F-H(3) G-H(4)

Está elegido a propósito para que el camino mínimo **no sea el directo**:
A→C cuesta 9 en línea recta, pero A→B→C cuesta 7. Si su Dijkstra devuelve 9,
la relajación de aristas está mal.

## Antes de entregar

- [ ] `./mvnw test` pasa
- [ ] no hay ninguna contraseña en el código ni en `application.properties`
- [ ] cada algoritmo implementado tiene al menos un test
- [ ] `docs/ENDPOINTS.md` documenta entrada y salida de cada endpoint
- [ ] el README explica **qué dominio eligieron y qué representa el costo**

Ver `GUIA-TPO.md` para el paso a paso.
