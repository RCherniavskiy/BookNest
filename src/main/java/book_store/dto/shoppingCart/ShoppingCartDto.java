package book_store.dto.shoppingCart;

import book_store.dto.cartItemDto.CartItemDto;
import lombok.Data;
import java.util.Set;

@Data
public class ShoppingCartDto {
    private  Long id;
    private  Long userId;
    private Set<CartItemDto> cartItems;
}
