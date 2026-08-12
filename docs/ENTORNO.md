# Entorno de trabajo — Neo4j + Spring Boot

Verificado el 2026-08-11 contra el servidor Neo4j que está corriendo en esta máquina.

## 1. Servidor Neo4j (ya corriendo)

| Dato | Valor |
|---|---|
| Producto | Neo4j **2026.07.0 enterprise** (Neo4j Desktop 2) |
| DBMS id | `dbms-55f58bb0-cb65-49a1-bbfd-790d86204add` |
| Home | `C:\Users\marco\.Neo4jDesktop2\Data\dbmss\dbms-55f58bb0-cb65-49a1-bbfd-790d86204add` |
| Runtime | Zulu JRE 21.0.11 (embebido en Desktop), modo `--console-mode` |
| Bolt | `neo4j://127.0.0.1:7687` (también `bolt://127.0.0.1:7687`) |
| HTTP / Browser | `http://127.0.0.1:7474` |
| HTTPS | deshabilitado (`server.https.enabled=false`) |
| Bind | **solo 127.0.0.1** — no accesible desde otra máquina |
| Base por defecto | `neo4j` (online). Además `system` |
| Auth | habilitada — usuario `neo4j`, password en `.env` (ver `.env.example`) |
| Cypher | `db.query.default_language=CYPHER_25` |
| Memoria | heap 512m–1G, pagecache 512m |
| Plugins | APOC 2026.07.0 (174 procs), GDS 2026.07.0 (471 procs), GenAI |
| Procs permitidos | `dbms.security.procedures.allowlist=apoc.*,genai.*,ai.*,gds.*` |
| Servicio Windows | `neo4j-relate-dbms-55f58bb0-cb65-49a1-bbfd-790d86204add` (lo maneja Desktop) |

Archivo de configuración: `<home>\conf\neo4j.conf`. Se arranca/para desde Neo4j Desktop 2.

## 2. Toolchain local

| Herramienta | Versión |
|---|---|
| JDK | Temurin **25.0.4** (`C:\Program Files\Eclipse Adoptium\jdk-25.0.4.7-hotspot`) |
| Maven | 3.9.11 vía wrapper (`./mvnw`, dists en `~/.m2/wrapper`) |
| Spring Boot | 3.5.3 (`spring-boot-starter-data-neo4j`, `-web`, `-validation`) |
| `java.version` del pom | 17 → se compila con `--release 17` sobre el JDK 25 |

Nota: el driver Netty imprime warnings de `sun.misc.Unsafe` en JDK 25. Son ruido, no rompen nada.

## 3. Configuración de la app

Cadena de resolución (de menor a mayor prioridad):

```
application.properties  →  .env (spring.config.import)  →  variables de entorno reales
```

`src/main/resources/application.properties`:

```properties
spring.config.import=optional:file:.env[.properties]

spring.neo4j.uri=${NEO4J_URI:neo4j://127.0.0.1:7687}
spring.neo4j.authentication.username=${NEO4J_USER:neo4j}
spring.neo4j.authentication.password=${NEO4J_PASSWORD:}

app.seed.enabled=true
logging.level.uade.prog3.tpo=INFO
```

`.env` (en la raíz del proyecto, **gitignoreado** por la regla `*.env`):

```
NEO4J_URI=neo4j://127.0.0.1:7687
NEO4J_USER=neo4j
NEO4J_PASSWORD=<la password real, ver .env.example>
```

Detalles que importan:

- `spring.config.import` con el hint `[.properties]` es lo que hace que Spring parsee un archivo llamado `.env`.
- La ruta es **relativa al directorio de trabajo**: correr desde la raíz del proyecto (`./mvnw spring-boot:run` o el run config del IDE con working dir = raíz). Desde otro directorio, la password queda vacía y el driver falla con `AuthenticationException`.
- `optional:` hace que la app arranque igual si no existe `.env` (por ejemplo en CI, donde se usan variables de entorno reales).
- Para apuntar a Aura sin tocar código: `NEO4J_URI=neo4j+s://xxxx.databases.neo4j.io` en `.env`.

## 4. Levantar y verificar

```bash
# la app (desde la raíz del proyecto)
./mvnw spring-boot:run          # Tomcat en :8080

# infraestructura OK si esto responde
curl "http://localhost:8080/api/grafo/resumen"
# {"ids":["A","B","C","D","E","F","G","H"],"vertices":8,"dirigido":true,"aristas":12}

# un algoritmo sin implementar responde 501 (esperado)
curl -i "http://localhost:8080/api/grafo/dfs?origen=A"
```

Consultar Neo4j sin pasar por la app (HTTP Query API v2):

```bash
curl -u neo4j:$NEO4J_PASSWORD -H 'Content-Type: application/json' \
  -d '{"statement":"MATCH (n) RETURN labels(n)[0] AS label, count(*) AS n"}' \
  http://127.0.0.1:7474/db/neo4j/query/v2
```

O el Browser en <http://127.0.0.1:7474>.

## 5. Estado de los datos

La base `neo4j` ya tiene el grafo de ejemplo cargado: **8 `Nodo`, 12 `Conexion`, 5 `Item`**.
Por eso `CargaInicial` loguea *"La base ya tiene datos. No se ejecuta la carga inicial."* y no
duplica nada. Para recargar desde cero:

```cypher
MATCH (n) DETACH DELETE n
```

y reiniciar la app. Cuando el grupo cargue su propio dominio: `app.seed.enabled=false`.

## 6. Pendiente de seguridad

La password quedó commiteada en `application.properties` en el commit inicial `64e2a4e`.
Sacarla del archivo (ya hecho) **no la borra del historial de git**. Si el repo se publica,
hay que rotar la password del usuario `neo4j` y/o reescribir el historial. Decisión del grupo.
