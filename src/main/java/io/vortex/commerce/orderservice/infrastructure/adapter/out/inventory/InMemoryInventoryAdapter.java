package io.vortex.commerce.orderservice.infrastructure.adapter.out.inventory;

import io.vortex.commerce.orderservice.domain.model.OrderItem;
import io.vortex.commerce.orderservice.domain.port.out.InventoryPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Esta es una implementación mock/en memoria del adaptador de inventario.
 * esta clase haría una llamada a otro microservicio
 * Para esta prueba, simulamos
 * que todas las operaciones son exitosas.
 */
@Component
@Slf4j
public class InMemoryInventoryAdapter implements InventoryPort {

    @Override
    public boolean hasSufficientStock(List<OrderItem> items) {
        log.info("Verificando stock para {} items. [MOCK] -> Siempre hay suficiente.", items.size());
        return true;
    }

    @Override
    public void reserveStock(List<OrderItem> items) {
        log.info("Reservando stock para {} items. [MOCK] -> Reserva exitosa.", items.size());
    }
}
