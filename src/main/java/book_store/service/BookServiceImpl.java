package book_store.service;

import book_store.dto.BookDto;
import book_store.dto.book.BookSearchParameters;
import book_store.dto.CreateBookRequestDto;
import book_store.dto.category.BookDtoWithoutCategoryIds;
import book_store.exception.EntityNotFoundException;
import book_store.mapper.BookMapper;
import book_store.model.Category;
import book_store.repository.book.BookRepository;
import book_store.repository.book.BookSpecificationBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import book_store.model.Book;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService{
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        Set<Long> categoryIds = requestDto.getCategoryIds();
        if (categoryIds != null && !categoryIds.isEmpty()) {
            Set<Category> categories = categoryIds.stream()
                    .map(id -> {
                        Category category = new Category();
                        category.setId(id);
                        return category;
                    } )
                    .collect(Collectors.toSet());
            book.setCategories(categories);
        }
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public BookDto findById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("Can`t find book by id" + id)
        );
        return bookMapper.toDto(book);
    }

    @Override
    public List<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto updateById(CreateBookRequestDto requestDto, Long id) {
        Book bookToUpdate = bookMapper.toModel(requestDto);
        bookToUpdate.setId(id);
        Book updatedBook = bookRepository.save(bookToUpdate);
        return bookMapper.toDto(updatedBook);
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public List<BookDto> search(BookSearchParameters params) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(params);
        return bookRepository.findAll(bookSpecification)
                .stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public List<BookDtoWithoutCategoryIds> getBooksByCategoryId(Long categoryId) {
        Category category = new Category();
        category.setId(categoryId);
        return bookRepository.findAllByCategoriesContaining(category).stream()
                .map(bookMapper::toDtoWithoutCategories)
                .collect(Collectors.toList());
    }
}
