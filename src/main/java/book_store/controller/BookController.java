package book_store.controller;

import book_store.dto.BookDto;
import book_store.dto.BookSearchParameters;
import book_store.dto.CreateBookRequestDto;
import book_store.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    @GetMapping
    public List<BookDto> getAll() {
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @PostMapping
    public BookDto createBook(@RequestBody CreateBookRequestDto requestDto) {
        return bookService.save(requestDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
     public void delete(@PathVariable Long id) {
        bookService.deleteById(id);
     }

    @PutMapping("/{id}")
    public BookDto updateBook(@PathVariable Long id, @RequestBody CreateBookRequestDto requestDto) {
        return bookService.updateById(requestDto, id);
    }

    @GetMapping("/search")
    public List<BookDto> search(BookSearchParameters searchParameters) {
        return bookService.search(searchParameters);
    }
}
