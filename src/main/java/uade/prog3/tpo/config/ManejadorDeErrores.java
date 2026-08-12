package uade.prog3.tpo.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uade.prog3.tpo.algorithm.PendienteDeImplementar;

import java.util.Map;

/**
 * Traduce excepciones a respuestas HTTP con un cuerpo entendible.
 *
 * Lo importante para el trabajo: un algoritmo sin implementar devuelve
 * 501 Not Implemented con un mensaje claro, en vez de un stack trace de 500.
 * Asi la aplicacion arranca y es demostrable desde el primer dia.
 */
@RestControllerAdvice
public class ManejadorDeErrores {

    @ExceptionHandler(PendienteDeImplementar.class)
    public ResponseEntity<Map<String, String>> pendiente(PendienteDeImplementar e) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(Map.of("estado", "PENDIENTE", "detalle", e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> datosInvalidos(IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .body(Map.of("estado", "ERROR", "detalle", e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> estadoInvalido(IllegalStateException e) {
        return ResponseEntity.unprocessableEntity()
                .body(Map.of("estado", "ERROR", "detalle", e.getMessage()));
    }
}
