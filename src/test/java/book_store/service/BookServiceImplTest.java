package book_store.service;

import book_store.dto.BookDto;
import book_store.dto.CreateBookRequestDto;
import book_store.dto.book.BookSearchParameters;
import book_store.exception.EntityNotFoundException;
import book_store.mapper.BookMapper;
import book_store.model.Book;
import book_store.model.Category;
import book_store.repository.book.BookRepository;
import book_store.repository.book.BookSpecificationBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import java.util.Collections;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;

    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_NewBookProvided_BookSavedSuccess() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        Book book = new Book();
        BookDto expectedDto = new BookDto();
        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expectedDto);
        when(bookRepository.save(book)).thenReturn(book);

        BookDto savedBook = bookService.save(requestDto);

        assertEquals(expectedDto, savedBook);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void save_CategoryIdsProvided_CategoriesAddedToBook() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Test Book");
        requestDto.setAuthor("Test Author");
        requestDto.setCategoryIds(Collections.singleton(1L));

        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("Test Author");

        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(new BookDto());

        BookDto savedBook = bookService.save(requestDto);

        verify(bookMapper, times(1)).toModel(requestDto);
        verify(bookRepository, times(1)).save(book);
        verify(bookMapper, times(1)).toDto(book);
        assertEquals(new BookDto(), savedBook);
    }

    @Test
    void findById_ExistingIdProvided_BookFoundSuccess() {
        long bookId = 1L;
        Book book = new Book();
        BookDto expectedDto = new BookDto();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(expectedDto);

        BookDto foundBook = bookService.findById(bookId);

        assertEquals(expectedDto, foundBook);
    }

    @Test
    void findById_NonExistingIdProvided_EntityNotFoundExceptionThrown() {
        long nonExistingId = 999L;
        when(bookRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookService.findById(nonExistingId));
    }

    @Test
    void findAll_ValidPageableProvided_AllBooksRetrievedSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));

        bookService.findAll(pageable);

        verify(bookRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void updateById_ExistingBookAndIdProvided_BookUpdatedSuccess() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        long bookId = 1L;
        Book bookToUpdate = new Book();
        Book updatedBook = new Book();
        BookDto expectedDto = new BookDto();
        when(bookMapper.toModel(requestDto)).thenReturn(bookToUpdate);
        when(bookRepository.save(bookToUpdate)).thenReturn(updatedBook);
        when(bookMapper.toDto(updatedBook)).thenReturn(expectedDto);

        BookDto updatedDto = bookService.updateById(requestDto, bookId);

        assertEquals(expectedDto, updatedDto);
    }

    @Test
    void deleteById_ExistingIdProvided_BookDeletedSuccess() {
        long bookId = 1L;

        bookService.deleteById(bookId);

        verify(bookRepository, times(1)).deleteById(bookId);
    }

    @Test
    void search_ValidSearchParametersProvided_BooksFoundSuccess() {
        String[] titles = {"title1", "title2"};
        String[] authors = {"author1", "author2"};
        BookSearchParameters params = new BookSearchParameters(titles, authors);

        when(bookRepository.findAll(ArgumentMatchers.<Specification<Book>>any())).thenReturn(Collections.emptyList());

        bookService.search(params);

        verify(bookRepository, times(1)).findAll(ArgumentMatchers.<Specification<Book>>any());
    }

    @Test
    void getBooksByCategoryId_ExistingCategoryIdProvided_BooksFoundSuccess() {

        long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);

        when(bookRepository.findAllByCategoriesContaining(any(Category.class))).thenReturn(Collections.emptyList());

        bookService.getBooksByCategoryId(categoryId);

        verify(bookRepository, times(1)).findAllByCategoriesContaining(any(Category.class));
    }
}