package book_store.repository.cartItem;

import book_store.model.Book;
import book_store.model.CartItem;
import book_store.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByShoppingCartAndBook(ShoppingCart shoppingCart, Book book);
}
