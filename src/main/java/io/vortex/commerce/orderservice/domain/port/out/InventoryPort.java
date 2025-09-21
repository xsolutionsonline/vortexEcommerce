package io.vortex.commerce.orderservice.domain.port.out;

import io.vortex.commerce.orderservice.domain.model.OrderItem;
import java.util.List;

public interface InventoryPort {
    /**
     * Verifica si hay suficiente stock para una lista de productos.
     * @param items La lista de items del pedido.
     * @return true si hay stock para todos, false en caso contrario.
     */
    boolean hasSufficientStock(List<OrderItem> items);

    /**
     * Reserva el stock para los items de un pedido.
     * @param items La lista de items del pedido a reservar.
     */
    void reserveStock(List<OrderItem> items);

     /**
     * Libera el stock para los items de un pedido reservado.
     * @param items La lista de items del pedido liberado.
     */

    void releaseStock(List<OrderItem> items);
}