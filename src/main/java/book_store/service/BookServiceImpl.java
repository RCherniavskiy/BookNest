package book_store.service;

import book_store.dto.BookDto;
import book_store.dto.CreateBookRequestDto;
import book_store.exception.EntityNotFoundException;
import book_store.mapper.BookMapper;
import book_store.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import book_store.model.Book;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService{
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public BookDto findById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("Can`t find employee by id" + id)
        );
        return bookMapper.toDto(book);
    }

    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto updateById(CreateBookRequestDto requestDto, Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("Can`t find employee by id" + id)
        );
        book.setTitle(requestDto.getTitle());
        book.setAuthor(requestDto.getAuthor());
        book.setIsbn(requestDto.getIsbn());
        book.setPrice(requestDto.getPrice());
        book.setDescription(requestDto.getDescription());
        book.setCoverImage(requestDto.getCoverImage());
        book = bookRepository.save(book);
        return bookMapper.toDto(book);
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }
}
