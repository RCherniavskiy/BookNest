package book_store.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.math.BigDecimal;

@Entity
@Setter
@Getter
@SQLDelete(sql = "Update orderItems SET is_deleted = TRUE WHERE id = ?")
@SQLRestriction(value = "is_deleted = FALSE")
@Table(name = "orderitems")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    @Column(nullable = false)
    private int quantity;
    @Column(nullable = false)
    private BigDecimal price;
    @Column(name = "is_deleted",nullable = false)
    private boolean isDeleted = false;
}
