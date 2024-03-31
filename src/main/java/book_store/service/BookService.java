package book_store.service;

import book_store.dto.BookDto;
import book_store.dto.book.BookSearchParameters;
import book_store.dto.CreateBookRequestDto;
import book_store.dto.category.BookDtoWithoutCategoryIds;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    BookDto findById(Long id);

    List<BookDto> findAll(Pageable pageable);

    BookDto updateById(CreateBookRequestDto requestDto, Long id);

    void deleteById(Long id);

    List<BookDto> search(BookSearchParameters params);

    List<BookDtoWithoutCategoryIds> getBooksByCategoryId(Long categoryId);
}
