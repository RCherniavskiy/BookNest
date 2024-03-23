package book_store.service;

import book_store.dto.BookDto;
import book_store.dto.book.BookSearchParameters;
import book_store.dto.CreateBookRequestDto;
import java.util.List;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    BookDto findById(Long id);

    List<BookDto> findAll();

    BookDto updateById(CreateBookRequestDto requestDto, Long id);

    void deleteById(Long id);

    public List<BookDto> search(BookSearchParameters params);
}
