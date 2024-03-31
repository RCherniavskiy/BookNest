package book_store.mapper;

import book_store.config.MapperConfig;
import book_store.dto.category.CategoryDto;
import book_store.dto.user.UserRegistrationRequestDto;
import book_store.dto.user.UserResponseDto;
import book_store.model.Category;
import book_store.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toUserResponseDto(User user);

    User toModel(UserRegistrationRequestDto requestDto);
}
