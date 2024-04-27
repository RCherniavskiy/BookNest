package book_store.service;

import book_store.dto.category.CategoryDto;
import book_store.exception.EntityNotFoundException;
import book_store.mapper.CategoryMapper;
import book_store.model.Category;
import book_store.repository.category.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void findAll_ValidInput_ReturnsListOfCategoryDto() {
        Pageable pageable = Pageable.ofSize(10).withPage(0);
        List<Category> categories = List.of(new Category(), new Category());
        when(categoryRepository.findAll(pageable)).thenReturn(new PageImpl<>(categories));
        when(categoryMapper.toDto(any())).thenReturn(new CategoryDto());

        List<CategoryDto> result = categoryService.findAll(pageable);

        assertEquals(2, result.size());
        verify(categoryRepository, times(1)).findAll(pageable);
        verify(categoryMapper, times(2)).toDto(any());
    }

    @Test
    void getById_ExistingCategoryId_ReturnsCategoryDto() {
        long categoryId = 1L;
        Category category = new Category();
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        CategoryDto expectedDto = new CategoryDto();
        when(categoryMapper.toDto(category)).thenReturn(expectedDto);
        CategoryDto result = categoryService.getById(categoryId);

        assertEquals(expectedDto, result);
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryMapper, times(1)).toDto(category);
    }

    @Test
    void getById_NonExistingCategoryId_ThrowsEntityNotFoundException() {
        long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.getById(categoryId));

        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    void save_ValidCategoryDto_ReturnsSavedCategoryDto() {
        CategoryDto categoryDto = new CategoryDto();
        Category category = new Category();
        when(categoryMapper.toEntity(categoryDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto result = categoryService.save(categoryDto);

        assertEquals(categoryDto, result);
        verify(categoryMapper, times(1)).toEntity(categoryDto);
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toDto(category);
    }

    @Test
    void update_ExistingCategoryIdAndValidCategoryDto_ReturnsUpdatedCategoryDto() {
        long categoryId = 1L;
        CategoryDto categoryDto = new CategoryDto();
        Category categoryToUpdate = new Category();
        when(categoryMapper.toEntity(categoryDto)).thenReturn(categoryToUpdate);
        when(categoryRepository.save(categoryToUpdate)).thenReturn(categoryToUpdate);
        when(categoryMapper.toDto(categoryToUpdate)).thenReturn(categoryDto);

        CategoryDto result = categoryService.update(categoryId, categoryDto);

        assertEquals(categoryDto, result);
        verify(categoryMapper, times(1)).toEntity(categoryDto);
        verify(categoryRepository, times(1)).save(categoryToUpdate); // Исправлено на save
        verify(categoryMapper, times(1)).toDto(categoryToUpdate);
    }

    @Test
    void deleteById_ExistingCategoryId_DeletesCategory() {
        long categoryId = 1L;
        categoryService.deleteById(categoryId);
        verify(categoryRepository, times(1)).deleteById(categoryId);
    }
}