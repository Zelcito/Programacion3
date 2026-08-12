package uade.prog3.tpo.model;

import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

/**
 * Arista PONDERADA entre dos nodos.
 *
 * El atributo costo es lo que habilita todos los algoritmos sobre grafos
 * con peso: Dijkstra, Prim, Kruskal, Floyd y UCS.
 *
 * Segun el dominio, costo puede representar distancia, tiempo, precio,
 * capacidad o cualquier magnitud que el grupo quiera minimizar o maximizar.
 * Lo importante es que sea un unico numero y que este documentado en el README
 * que significa y en que unidad esta expresado.
 */
@RelationshipProperties
public class Conexion {

    @RelationshipId
    private Long id;

    private double costo;

    @TargetNode
    private Nodo destino;

    public Conexion() {
    }

    public Conexion(Nodo destino, double costo) {
        this.destino = destino;
        this.costo = costo;
    }

    public Long getId() { return id; }

    public double getCosto() { return costo; }
    public void setCosto(double costo) { this.costo = costo; }

    public Nodo getDestino() { return destino; }
    public void setDestino(Nodo destino) { this.destino = destino; }
}
