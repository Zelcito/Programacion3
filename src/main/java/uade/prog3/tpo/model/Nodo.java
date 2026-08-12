package uade.prog3.tpo.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.OUTGOING;

/**
 * Vertice generico del grafo.
 *
 * ESTE ES EL PUNTO QUE CADA GRUPO DEBE ADAPTAR A SU DOMINIO.
 *
 * Ejemplos de especializacion:
 *   - Red logistica  -> Nodo = deposito o punto de entrega;  tipo = "DEPOSITO" | "CLIENTE"
 *   - Transporte     -> Nodo = estacion;                     tipo = "SUBTE" | "TREN"
 *   - Red social     -> Nodo = usuario;                      tipo = "PERSONA" | "ORGANIZACION"
 *
 * Lo unico que NO se debe romper es que exista una relacion con COSTO NUMERICO
 * entre nodos: sin peso en las aristas no se pueden implementar Dijkstra,
 * Prim ni Kruskal, que son 3 de los 10 puntos del trabajo.
 */
@Node("Nodo")
public class Nodo {

    @Id
    private String id;

    private String nombre;

    /** Etiqueta libre para que el grupo distinga subtipos de su dominio. */
    private String tipo;

    /**
     * Atributo numerico propio del vertice (no de la arista).
     * Segun el dominio puede ser demanda, capacidad, poblacion, prioridad...
     * Se usa, por ejemplo, en los algoritmos greedy y de programacion dinamica.
     */
    private double valor;

    @Relationship(type = "CONECTA", direction = OUTGOING)
    private List<Conexion> conexiones = new ArrayList<>();

    public Nodo() {
    }

    public Nodo(String id, String nombre, String tipo, double valor) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.valor = valor;
    }

    public void conectar(Nodo destino, double costo) {
        this.conexiones.add(new Conexion(destino, costo));
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public List<Conexion> getConexiones() { return conexiones; }
    public void setConexiones(List<Conexion> conexiones) { this.conexiones = conexiones; }

    @Override
    public String toString() {
        return "Nodo{id='" + id + "', nombre='" + nombre + "', tipo='" + tipo + "'}";
    }
}
