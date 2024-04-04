package book_store.service;

import book_store.dto.order.OrderDto;
import book_store.dto.order.OrderRequest;
import book_store.dto.orderItem.OrderItemDto;
import book_store.exception.EntityNotFoundException;
import book_store.mapper.OrderItemMapper;
import book_store.mapper.OrderMapper;
import book_store.model.*;
import book_store.repository.book.BookRepository;
import book_store.repository.order.OrderRepository;
import book_store.repository.shoppingCart.ShoppingCartRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Transactional
    @Override
    public OrderDto orderReturn(OrderRequest request, User user) {
        Order order = new Order();
        order.setShippingAddress(request.getShippingAddress());
        order.setStatus(Order.Status.PENDING);
        BigDecimal total = BigDecimal.ZERO;
        ShoppingCart shoppingCart = shoppingCartRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Shopping cart not found for user: " + user.getUsername()));
        Set<OrderItem> orderItems = new HashSet<>();
        for (CartItem cartItem : shoppingCart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setBook(cartItem.getBook());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOrder(order);
            orderItem.setPrice(cartItem.getBook().getPrice());
            orderItems.add(orderItem);
            total = total.add(cartItem.getBook().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }
        order.setTotal(total);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderItems(orderItems);
        order.setUser(user);
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    @Transactional
    @Override
    public List<OrderDto> findAll(User user, Pageable pageable) {
        Page<Order> ordersPage = orderRepository.findByUser(user, pageable);
        List<Order> orders = ordersPage.getContent();
        return orderMapper.toDtoList(orders);
    }

    @Transactional
    @Override
    public ResponseEntity<OrderDto> updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));
        order.setStatus(Order.Status.valueOf(status));
        orderRepository.save(order);
        OrderDto updatedOrderDto = orderMapper.toDto(order);
        return ResponseEntity.ok(updatedOrderDto);
    }

    @Transactional
    @Override
    public List<OrderItemDto> getAllOrderItems(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));
        Set<OrderItem> orderItems = order.getOrderItems();
        return orderItems.stream()
                .map(orderItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public OrderItemDto getOrderItem(Long orderId, Long itemId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));
        OrderItem orderItem = order.getOrderItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Order item not found with id: " + itemId));
        return orderItemMapper.toDto(orderItem);
    }
}
