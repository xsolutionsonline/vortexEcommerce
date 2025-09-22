package io.vortex.commerce.orderservice.infrastructure.in.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vortex.commerce.orderservice.domain.model.Order;
import io.vortex.commerce.orderservice.domain.model.OrderItem;
import io.vortex.commerce.orderservice.domain.model.OrderStatus;
import io.vortex.commerce.orderservice.domain.port.in.*;
import io.vortex.commerce.orderservice.infrastructure.adapter.in.web.controller.OrderControllerV2;
import io.vortex.commerce.orderservice.infrastructure.adapter.in.web.dto.CreateOrderRequest;
import io.vortex.commerce.orderservice.infrastructure.adapter.in.web.dto.OrderItemRequest;
import io.vortex.commerce.orderservice.infrastructure.adapter.in.web.dto.UpdateStatusRequest;
import io.vortex.commerce.orderservice.infrastructure.adapter.in.web.mapper.OrderApiMapper;
import io.vortex.commerce.orderservice.infrastructure.adapter.in.web.mapper.OrderApiMapperImpl;
import io.vortex.commerce.orderservice.infrastructure.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderControllerV2.class)
@Import(OrderApiMapperImpl.class)
class OrderControllerV2IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateOrderUseCase createOrderUseCase;

    @MockBean
    private UpdateOrderStatusUseCase updateOrderStatusUseCase;

    @MockBean
    private FindOrderByIdUseCase findOrderByIdUseCase;

    @MockBean
    private FindOrdersUseCase findOrdersUseCase;

    @MockBean
    private CancelOrderUseCase cancelOrderUseCase; // <-- Mock para el nuevo caso de uso

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @TestConfiguration
    @EnableMethodSecurity
    static class TestSecurityConfiguration {
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                    .formLogin(form -> form.disable())
                    .httpBasic(Customizer.withDefaults());
            return http.build();
        }
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createOrder_shouldReturnCreated() throws Exception {
        // Arrange
        var itemRequest = new OrderItemRequest(1L, 2, new BigDecimal("50.00"));
        var request = new CreateOrderRequest(1L, List.of(itemRequest));

        var createdOrder = Order.builder()
                .id(1L)
                .customerId(1L)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .items(List.of(OrderItem.builder().productId(1L).quantity(2).price(new BigDecimal("50.00")).build()))
                .build();

        when(createOrderUseCase.createOrder(any(CreateOrderCommand.class))).thenReturn(createdOrder);

        // Act & Assert
        mockMvc.perform(post("/api/v2/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value(OrderStatus.PENDING.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateOrderStatus_shouldReturnOk() throws Exception {
        // Arrange
        Long orderId = 1L;
        var request = new UpdateStatusRequest(OrderStatus.SHIPPED);
        var updatedOrder = Order.builder().id(orderId).customerId(1L).status(OrderStatus.SHIPPED).orderDate(LocalDateTime.now()).build();

        when(updateOrderStatusUseCase.updateOrderStatus(orderId, OrderStatus.SHIPPED)).thenReturn(updatedOrder);

        // Act & Assert
        mockMvc.perform(put("/api/v2/orders/{orderId}/status", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.status").value(OrderStatus.SHIPPED.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getOrderById_whenOrderExists_shouldReturnOk() throws Exception {
        // Arrange
        Long orderId = 1L;
        var order = Order.builder().id(orderId).customerId(1L).status(OrderStatus.PENDING).orderDate(LocalDateTime.now()).build();

        when(findOrderByIdUseCase.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        mockMvc.perform(get("/api/v2/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getOrderById_whenOrderDoesNotExist_shouldReturnNotFound() throws Exception {
        // Arrange
        Long orderId = 99L;
        when(findOrderByIdUseCase.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/v2/orders/{orderId}", orderId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findOrders_shouldReturnPageOfOrders() throws Exception {
        // Arrange
        Long customerId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        var order = Order.builder().id(1L).customerId(customerId).status(OrderStatus.PENDING).orderDate(LocalDateTime.now()).build();
        Page<Order> orderPage = new PageImpl<>(List.of(order), pageable, 1);

        when(findOrdersUseCase.findOrders(any(FindOrdersUseCase.FindOrdersQuery.class), any(Pageable.class))).thenReturn(orderPage);

        // Act & Assert
        mockMvc.perform(get("/api/v2/orders")
                        .param("customerId", customerId.toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    
    @Test
    @WithMockUser(roles = "ADMIN")
    void cancelOrder_shouldReturnOk() throws Exception {
        // Arrange
        Long orderId = 1L;
        var cancelledOrder = Order.builder()
                .id(orderId)
                .customerId(1L)
                .status(OrderStatus.CANCELLED)
                .orderDate(LocalDateTime.now())
                .build();

        when(cancelOrderUseCase.cancelOrder(orderId)).thenReturn(cancelledOrder);

        // Act & Assert
        mockMvc.perform(delete("/api/v2/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.status").value(OrderStatus.CANCELLED.toString()));
    }

    @Test
    @WithMockUser(roles = "USER")
    void cancelOrder_whenNotAdmin_shouldReturnForbidden() throws Exception {
        // Arrange
        Long orderId = 1L;

        // Act & Assert
        mockMvc.perform(delete("/api/v2/orders/{orderId}", orderId))
                .andExpect(status().isForbidden());
    }

    @Test
    void cancelOrder_whenUnauthenticated_shouldReturnUnauthorized() throws Exception {
        // Arrange
        Long orderId = 1L;

        // Act & Assert
        mockMvc.perform(delete("/api/v2/orders/{orderId}", orderId))
                .andExpect(status().isUnauthorized());
    }

    
    @Test
    @WithMockUser(roles = "USER")
    void createOrder_whenNotAdmin_shouldReturnForbidden() throws Exception {
        // Arrange
        var request = new CreateOrderRequest(1L, Collections.emptyList());

        // Act & Assert
        mockMvc.perform(post("/api/v2/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createOrder_whenUnauthenticated_shouldReturnUnauthorized() throws Exception {
        // Arrange
        var request = new CreateOrderRequest(1L, Collections.emptyList());

        // Act & Assert
        mockMvc.perform(post("/api/v2/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}