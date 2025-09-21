package io.vortex.commerce.orderservice.infrastructure.adapter.out.inventory;

import io.vortex.commerce.orderservice.domain.constants.ErrorMessages;
import io.vortex.commerce.orderservice.domain.exception.ConcurrencyConflictException;
import io.vortex.commerce.orderservice.domain.exception.ProductNotFoundException;
import io.vortex.commerce.orderservice.domain.model.OrderItem;
import io.vortex.commerce.orderservice.domain.port.out.InventoryPort;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.entity.ProductEntity;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.product.ProductCacheAdapter;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.repository.JpaProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component("databaseInventoryAdapter")
@RequiredArgsConstructor
@Slf4j
public class DatabaseInventoryAdapter implements InventoryPort {

    private final JpaProductRepository productRepository;
    private final ProductCacheAdapter productCacheAdapter;

    @Override
    public boolean hasSufficientStock(List<OrderItem> items) {
        for (OrderItem item : items) {
            var product = productCacheAdapter.findProductById(item.getProductId());
            if (product.isEmpty() || product.get().getStock() < item.getQuantity()) {
                return false;
            }
        }
        return true;
    }

    @Override
    @Transactional
    public void reserveStock(List<OrderItem> items) {
        try {
            for (OrderItem item : items) {
                ProductEntity product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new ProductNotFoundException(String.format(ErrorMessages.PRODUCT_NOT_FOUND, item.getProductId())));

                if (product.getStock() < item.getQuantity()) {
                    throw new IllegalStateException(String.format(ErrorMessages.INSUFFICIENT_STOCK_FOR_PRODUCT, item.getProductId()));
                }

                product.setStock(product.getStock() - item.getQuantity());
                productRepository.save(product);
            }
        } catch (ObjectOptimisticLockingFailureException e) {
            log.warn("Race condition detected while reserving stock. Operation will be rolled back.", e);
            throw new ConcurrencyConflictException(ErrorMessages.CONCURRENCY_ERROR, e);
        }
    }

    @Override
    @Transactional
    public void releaseStock(List<OrderItem> items) {
        for (OrderItem item : items) {
            ProductEntity product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(String.format(ErrorMessages.PRODUCT_NOT_FOUND, item.getProductId())));
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }
    }
}
