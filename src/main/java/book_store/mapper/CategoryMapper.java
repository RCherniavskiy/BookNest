package book_store.mapper;

import book_store.config.MapperConfig;
import book_store.dto.category.CategoryDto;
import book_store.model.Category;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);
    Category toEntity(CategoryDto categoryDTO);
}
