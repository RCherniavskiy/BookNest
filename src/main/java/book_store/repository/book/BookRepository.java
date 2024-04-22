package book_store.repository.book;

import book_store.model.Book;
import book_store.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    List<Book> findAllByCategoriesContaining(Category category);

    @EntityGraph(attributePaths = "categories")
    List<Book> findAll(Specification<Book> spec);

    @EntityGraph(attributePaths = "categories")
    Page<Book> findAll(Pageable pageable);
}
