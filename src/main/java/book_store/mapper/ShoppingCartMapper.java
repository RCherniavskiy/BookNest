package book_store.mapper;

import book_store.config.MapperConfig;
import book_store.dto.cartItemDto.CartItemDto;
import book_store.dto.shoppingCart.ShoppingCartDto;
import book_store.model.CartItem;
import book_store.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(config = MapperConfig.class)
public interface ShoppingCartMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "cartItems", target = "cartItems")
    ShoppingCartDto toDto(ShoppingCart shoppingCart);


    default Set<CartItemDto> mapCartItemsDto(Set<CartItem> cartItems) {
        if (cartItems == null) {
            return null;
        }
        return cartItems.stream()
                .map(cartItem -> {
                    CartItemDto cartItemDto = new CartItemDto();
                    cartItemDto.setId(cartItem.getId());
                    cartItemDto.setBookId(cartItem.getBook().getId());
                    cartItemDto.setBookTitle(cartItem.getBook().getTitle());
                    cartItemDto.setQuantity(cartItem.getQuantity());
                    return cartItemDto;
                })
                .collect(Collectors.toSet());
    }
}
