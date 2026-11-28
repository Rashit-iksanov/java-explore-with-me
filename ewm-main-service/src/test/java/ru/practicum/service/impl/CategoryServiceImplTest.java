package ru.practicum.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void createCategory_shouldSaveUniqueCategory() {
        NewCategoryDto request = new NewCategoryDto("Концерты");
        when(categoryRepository.existsByName("Концерты")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(new Category(1L, "Концерты"));

        CategoryDto result = categoryService.createCategory(request);

        assertEquals("Концерты", result.getName());
        verify(categoryRepository).save(any());
    }

    @Test
    void createCategory_shouldThrowConflictWhenNameExists() {
        NewCategoryDto request = new NewCategoryDto("Концерты");
        when(categoryRepository.existsByName("Концерты")).thenReturn(true);

        assertThrows(ConflictException.class, () -> categoryService.createCategory(request));
    }

    @Test
    void deleteCategory_shouldThrowConflictWhenCategoryNotEmpty() {
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(eventRepository.existsByCategoryId(1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> categoryService.deleteCategory(1L));
        verify(categoryRepository, never()).deleteById(any());
    }

    @Test
    void deleteCategory_shouldDeleteEmptyCategory() {
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(eventRepository.existsByCategoryId(1L)).thenReturn(false);

        categoryService.deleteCategory(1L);

        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void updateCategory_shouldUpdateWhenNewNameIsUnique() {
        Category category = new Category(1L, "Концерты");
        CategoryDto dto = new CategoryDto(1L, "Выставки");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByName("Выставки")).thenReturn(false);
        when(categoryRepository.save(any())).thenReturn(new Category(1L, "Выставки"));

        CategoryDto result = categoryService.updateCategory(1L, dto);

        assertEquals("Выставки", result.getName());
    }

    @Test
    void getCategoryById_shouldThrowNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.getCategoryById(99L));
    }
}