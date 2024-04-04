package book_store.mapper;

import book_store.config.MapperConfig;
import book_store.dto.orderItem.OrderItemDto;
import book_store.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {

    @Mapping(source = "book.id", target = "bookId")
    OrderItemDto toDto(OrderItem orderItem);
}
