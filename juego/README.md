# La Ruta del Temple — juego

Prototipo del juego que usa el mismo grafo del dominio (las 20 ciudades y 36 rutas
del README principal). **Todavía no forma parte del TPO evaluado**: está acá para
que lo tengamos todos y se nos ocurran ideas.

## Cómo abrirlo

Doble clic en `ruta-del-temple.html` y se abre en el navegador. No hace falta
instalar nada; solo necesita internet la primera vez para cargar las fuentes.

## Qué tiene

- Mochila inicial y mercados: greedy contra programación dinámica
- Mapa del Mediterráneo con niebla, zoom y rutas por camino, mar y montaña
- Viajes en primera persona con eventos (templarios, bandidos, peregrinos, etc.)
- Objetos templarios que usan BFS, DFS, Dijkstra, Prim, Kruskal, Floyd y backtracking
- Reparto de escoltas con Branch & Bound
- Caballo con edad que cambia según cómo te va

## Controles

- **Mapa:** clic en una ciudad para viajar (o teclas 1-6), rueda o +/− para zoom,
  arrastrar para moverse, Q alforja, V mochila, L diario, H ayuda
- **Viaje:** espacio para apurar el paso, 1/2 o Enter para elegir en los eventos
- **Título:** Enter continúa la partida en curso (o empieza una si no hay), N empieza una nueva
- **General:** F pantalla completa, M sonido, Esc menú

Los algoritmos del juego están escritos en JavaScript dentro del HTML. Los que
cuentan para el TPO son los de Java en `src/main/java/.../algorithm`.
