package book_store.repository;

import book_store.dto.BookDto;
import book_store.dto.CreateBookRequestDto;
import book_store.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
}
