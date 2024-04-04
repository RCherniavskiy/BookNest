package book_store.controller;

import book_store.dto.order.OrderDto;
import book_store.dto.order.OrderRequest;
import book_store.dto.order.UpdateOrderStatusRequest;
import book_store.dto.orderItem.OrderItemDto;
import book_store.model.User;
import book_store.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "OrderController management", description = "Endpoints for managing orders")
@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Place an order", description = "Endpoint for placing an order")
    @PostMapping
    public OrderDto placeOrder(@RequestBody OrderRequest request, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return orderService.orderReturn(request, user);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get all orders", description = "Endpoint for retrieving user's order history")
    @GetMapping
    public List<OrderDto> getOrderHistory(Pageable pageable, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return orderService.findAll(user, pageable);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update order status", description = "Endpoint for updating the status of an order")
    @PatchMapping("/{id}")
    public ResponseEntity<OrderDto> updateOrderStatus(@PathVariable Long id, @RequestBody UpdateOrderStatusRequest request) {
        ResponseEntity<OrderDto> responseEntity = orderService.updateOrderStatus(id, request.getStatus());
        OrderDto updatedOrderDto = responseEntity.getBody();
        return ResponseEntity.ok(updatedOrderDto);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Retrieve all OrderItems for a specific order", description = "Endpoint for retrieving all OrderItems for a specific order")
    @GetMapping("/{orderId}/items")
    public ResponseEntity<List<OrderItemDto>> getAllOrderItemsForOrder(@PathVariable Long orderId) {
        List<OrderItemDto> orderItems = orderService.getAllOrderItems(orderId);
        return ResponseEntity.ok(orderItems);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Retrieve a specific OrderItem within an order", description = "Endpoint for retrieving a specific OrderItem within an order")
    @GetMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<OrderItemDto> getOrderItemForOrder(@PathVariable Long orderId, @PathVariable Long itemId) {
        OrderItemDto orderItem = orderService.getOrderItem(orderId, itemId);
        return ResponseEntity.ok(orderItem);
    }
}
