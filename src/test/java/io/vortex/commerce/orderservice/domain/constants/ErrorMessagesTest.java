package io.vortex.commerce.orderservice.domain.constants;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

class ErrorMessagesTest {

    @Test
    @DisplayName("Should be a non-instantiable utility class")
    void shouldBeNonInstantiable() throws NoSuchMethodException {
        // Arrange
        Constructor<ErrorMessages> constructor = ErrorMessages.class.getDeclaredConstructor();

        // Assert
        assertTrue(Modifier.isPrivate(constructor.getModifiers()), "The constructor should be private.");

        constructor.setAccessible(true);

        // Assert that calling the constructor throws an exception
        Exception exception = assertThrows(InvocationTargetException.class, constructor::newInstance,
                "The private constructor should throw an exception if called via reflection.");

        // Assert the cause of the exception
        assertEquals(IllegalStateException.class, exception.getCause().getClass());
        assertEquals("Utility class", exception.getCause().getMessage());
    }

    @Test
    @DisplayName("Should contain correct and non-null error message constants")
    void shouldContainCorrectAndNonNullErrorMessages() {
        // Assert
        assertEquals("Producto con ID %d no encontrado.", ErrorMessages.PRODUCT_NOT_FOUND);
        assertEquals("Stock insuficiente para el producto con ID: %s", ErrorMessages.INSUFFICIENT_STOCK_FOR_PRODUCT);
        assertEquals("Orden con ID %d no encontrada.", ErrorMessages.ORDER_NOT_FOUND);
        assertEquals("Transición de estado inválida a: %s", ErrorMessages.INVALID_STATUS_TRANSITION);
        assertEquals("La orden no se puede procesar porque no está en estado PENDIENTE. Estado actual: %s", ErrorMessages.CANNOT_PROCESS_ORDER);
        assertEquals("La orden no se puede enviar porque no está en estado PROCESANDO. Estado actual: %s", ErrorMessages.CANNOT_SHIP_ORDER);
        assertEquals("La orden no se puede entregar porque no está en estado ENVIADO. Estado actual: %s", ErrorMessages.CANNOT_DELIVER_ORDER);
        assertEquals("No se puede cancelar una orden que ya ha sido enviada o entregada.", ErrorMessages.CANNOT_CANCEL_ORDER);
        assertEquals("Ocurrió un error inesperado.", ErrorMessages.GENERIC_ERROR);
        assertEquals("La orden fue modificada por otra transacción. Por favor, refresca los datos e inténtalo de nuevo.", ErrorMessages.CONCURRENCY_ERROR);
    }
}