package book_store.repository.shoppingCart;

import book_store.model.ShoppingCart;
import book_store.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    Page<ShoppingCart> findByUserId(Long userId, Pageable pageable);

    Optional<ShoppingCart> findByUser(User currentUser);
}
