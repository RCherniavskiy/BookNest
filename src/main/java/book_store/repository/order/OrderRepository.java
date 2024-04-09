package book_store.repository.order;

import book_store.model.Order;
import book_store.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = "orderItems")
    Page<Order> findByUser(User user, Pageable pageable);

    @EntityGraph(attributePaths = "orderItems")
    Optional<Order> findById(Long id);
}
