package book_store.mapper;

import book_store.config.MapperConfig;
import book_store.dto.BookDto;
import book_store.dto.CreateBookRequestDto;
import book_store.dto.category.BookDtoWithoutCategoryIds;
import book_store.model.Book;
import org.mapstruct.*;
import book_store.model.Category;
import java.util.stream.Collectors;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    @Mapping(target = "categories", ignore = true)
    Book toModel(CreateBookRequestDto requestDto);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    @AfterMapping
    default void setCategoryIds(@MappingTarget BookDto bookDto, Book book) {
        bookDto.setCategoryIds(book.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toSet()));
    }

    @Named("bookFromId")
    default Book bookFromId(Long id) {
        if (id == null) {
            return null;
        }
        Book book = new Book();
        book.setId(id);
        return book;
    }
}
