package book_store.service;

import book_store.dto.shoppingCart.AddToCartRequest;
import book_store.dto.shoppingCart.ShoppingCartDto;
import book_store.dto.cartItemDto.UpdateCartItemRequest;
import book_store.model.User;
import org.springframework.data.domain.Pageable;

public interface ShoppingCartService {
    ShoppingCartDto findUserShoppingCart(User user, Pageable pageable);

    ShoppingCartDto addBookToCart(AddToCartRequest request, User user);

    ShoppingCartDto updateCartItemQuantity(Long cartItemId, UpdateCartItemRequest request);

    void removeBookFromCart(Long cartItemId);
}
