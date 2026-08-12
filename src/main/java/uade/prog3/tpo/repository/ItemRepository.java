package uade.prog3.tpo.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import uade.prog3.tpo.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends Neo4jRepository<Item, String> {

    List<Item> findByNodoId(String nodoId);
}
