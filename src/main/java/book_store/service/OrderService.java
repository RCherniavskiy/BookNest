package book_store.service;

import book_store.dto.order.OrderDto;
import book_store.dto.order.OrderRequest;
import book_store.dto.orderItem.OrderItemDto;
import book_store.model.Order;
import book_store.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import java.util.List;

public interface OrderService {
    OrderDto orderReturn(OrderRequest requests, User user);

    List<OrderDto> findAll(User user, Pageable pageable);

    OrderDto updateOrderStatus(Long id, Order.Status status);

    List<OrderItemDto> getAllOrderItems(Long orderId);

    OrderItemDto getOrderItem(Long orderId, Long itemId);
}
