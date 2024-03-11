package book_store.controller;

import book_store.dto.BookDto;
import book_store.dto.BookSearchParameters;
import book_store.dto.CreateBookRequestDto;
import book_store.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Tag(name = "Book management", description = "Endpoint to managing books")
@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    @Operation(summary = "Get all books", description = "Get a list of all available books")
    @GetMapping
    public List<BookDto> getAll(Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @Operation(summary = "Get book by ID", description = "get book by ID")
    @GetMapping("/{id}")
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @Operation(summary = "Create a new book", description = "Creat a new book")
    @PostMapping
    public BookDto createBook(@RequestBody @Valid CreateBookRequestDto requestDto) {
        return bookService.save(requestDto);
    }

    @Operation(summary = "Delete book", description = "Delete book")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
     public void delete(@PathVariable Long id) {
        bookService.deleteById(id);
     }

    @Operation(summary = "Update book", description = "Update book")
    @PutMapping("/{id}")
    public BookDto updateBook(@PathVariable Long id, @RequestBody @Valid CreateBookRequestDto requestDto) {
        return bookService.updateById(requestDto, id);
    }

    @Operation(summary = "Search book", description = "Search book by parameter")
    @GetMapping("/search")
    public List<BookDto> search(BookSearchParameters searchParameters) {
        return bookService.search(searchParameters);
    }
}
