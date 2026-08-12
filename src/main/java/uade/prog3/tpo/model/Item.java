package uade.prog3.tpo.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

/**
 * Entidad auxiliar con un par (peso, valor).
 *
 * Existe para dar soporte a los problemas de seleccion bajo restriccion:
 * mochila fraccional con greedy, mochila 0/1 con programacion dinamica,
 * y seleccion optima con Branch and Bound.
 *
 * Cada grupo la renombra segun su dominio: paquete a cargar en un camion,
 * producto a stockear, tarea a asignar, inversion a realizar.
 */
@Node("Item")
public class Item {

    @Id
    private String id;

    private String nombre;

    /** Lo que el item CONSUME de la capacidad disponible. */
    private double peso;

    /** Lo que el item APORTA. Es lo que se busca maximizar. */
    private double valor;

    /** Id del Nodo donde se origina o al que pertenece el item. Puede ser null. */
    private String nodoId;

    public Item() {
    }

    public Item(String id, String nombre, double peso, double valor, String nodoId) {
        this.id = id;
        this.nombre = nombre;
        this.peso = peso;
        this.valor = valor;
        this.nodoId = nodoId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public String getNodoId() { return nodoId; }
    public void setNodoId(String nodoId) { this.nodoId = nodoId; }

    /** Criterio habitual de ordenamiento en los algoritmos greedy. */
    public double ratioValorPeso() {
        return peso == 0 ? Double.MAX_VALUE : valor / peso;
    }

    @Override
    public String toString() {
        return "Item{id='" + id + "', nombre='" + nombre + "', peso=" + peso + ", valor=" + valor + "}";
    }
}
