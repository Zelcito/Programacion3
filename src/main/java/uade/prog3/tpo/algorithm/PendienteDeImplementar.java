package uade.prog3.tpo.algorithm;

/**
 * Se lanza desde los algoritmos que el grupo todavia no implemento.
 * El manejador global la traduce a un HTTP 501 Not Implemented,
 * asi la aplicacion arranca y responde desde el dia uno.
 */
public class PendienteDeImplementar extends RuntimeException {

    public PendienteDeImplementar(String algoritmo) {
        super("Todavia no esta implementado: " + algoritmo);
    }
}
