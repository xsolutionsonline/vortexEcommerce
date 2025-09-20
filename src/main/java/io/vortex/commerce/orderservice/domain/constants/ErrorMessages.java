package io.vortex.commerce.orderservice.domain.constants;

public final class ErrorMessages {

    public static final String PRODUCT_NOT_FOUND = "Producto con ID %d no encontrado.";

    public static final String ORDER_NOT_FOUND = "Orden con ID %d no encontrada.";
    public static final String INSUFFICIENT_STOCK = "Stock insuficiente para uno o más artículos.";
    public static final String INVALID_STATUS_TRANSITION = "Transición de estado inválida a: %s";

    public static final String CANNOT_PROCESS_ORDER = "La orden no se puede procesar porque no está en estado PENDIENTE. Estado actual: %s";
    public static final String CANNOT_SHIP_ORDER = "La orden no se puede enviar porque no está en estado PROCESANDO. Estado actual: %s";
    public static final String CANNOT_DELIVER_ORDER = "La orden no se puede entregar porque no está en estado ENVIADO. Estado actual: %s";
    public static final String CANNOT_CANCEL_ORDER = "No se puede cancelar una orden que ya ha sido enviada o entregada.";

    public static final String GENERIC_ERROR = "Ocurrió un error inesperado.";

    public static final String CONCURRENCY_ERROR = "La orden fue modificada por otra transacción. Por favor, refresca los datos e inténtalo de nuevo.";

}
