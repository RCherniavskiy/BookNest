package book_store.mapper;

import book_store.config.MapperConfig;
import book_store.dto.orderItem.OrderItemDto;
import book_store.model.CartItem;
import book_store.model.OrderItem;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {

    @Mapping(source = "book.id", target = "bookId")
    @Named("toDto")
    OrderItemDto toDto(OrderItem orderItem);

    @IterableMapping(qualifiedByName = "toDto")
    List<OrderItemDto> toDtoList(Set<OrderItem> orderItems);
}
