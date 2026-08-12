package uade.prog3.tpo.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import uade.prog3.tpo.model.Nodo;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio BLOQUEANTE (no reactivo).
 *
 * Se eligio Neo4jRepository y no ReactiveNeo4jRepository a proposito: en esta
 * materia el foco esta en los algoritmos, y mezclar Mono/Flux agrega
 * complejidad que no aporta nada al objetivo de aprendizaje.
 *
 * findAll() ya trae cada Nodo con su lista de conexiones cargada, porque
 * Conexion esta mapeada con @Relationship en la entidad. No hace falta
 * ninguna consulta Cypher extra para armar el grafo.
 */
@Repository
public interface NodoRepository extends Neo4jRepository<Nodo, String> {

    Optional<Nodo> findByNombre(String nombre);

    List<Nodo> findByTipo(String tipo);
}
