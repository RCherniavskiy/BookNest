package book_store.mapper;

import book_store.config.MapperConfig;
import book_store.dto.order.OrderDto;
import book_store.dto.orderItem.OrderItemDto;
import book_store.model.Order;
import book_store.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(config = MapperConfig.class)
public interface OrderMapper {
    @Mapping(source = "user.id", target = "userId")
    List<OrderDto> toDtoList(List<Order> orders);

    @Mapping(source = "user.id", target = "userId")
    OrderDto toDto(Order orders);

    @Mapping(source = "book.id", target = "bookId")
    OrderItemDto mapOrderItem(OrderItem orderItem);
}
