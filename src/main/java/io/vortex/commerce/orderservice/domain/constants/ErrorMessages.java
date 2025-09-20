package io.vortex.commerce.orderservice.domain.constants;

public final class ErrorMessages {

    public static final String PRODUCT_NOT_FOUND = "Product with ID %d not found.";

    public static final String ORDER_NOT_FOUND = "Order with ID %d not found.";
    public static final String INSUFFICIENT_STOCK = "Insufficient stock for one or more items.";
    public static final String INVALID_STATUS_TRANSITION = "Invalid status transition to: %s";

    public static final String CANNOT_PROCESS_ORDER = "Order cannot be processed because it is not in PENDING state. Current state: %s";
    public static final String CANNOT_SHIP_ORDER = "Order cannot be shipped because it is not in PROCESSING state. Current state: %s";
    public static final String CANNOT_DELIVER_ORDER = "Order cannot be delivered because it is not in SHIPPED state. Current state: %s";
    public static final String CANNOT_CANCEL_ORDER = "Cannot cancel an order that has already been shipped or delivered.";

    public static final String GENERIC_ERROR = "An unexpected error occurred.";
}

