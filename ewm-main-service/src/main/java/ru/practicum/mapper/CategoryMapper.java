package ru.practicum.mapper;

import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewUserRequest;
import ru.practicum.model.Category;

public class CategoryMapper {

    public static CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }

    public static Category toCategory(NewUserRequest request) {
        return new Category(null, request.getName());
    }

    public static Category toCategory(CategoryDto dto) {
        return new Category(dto.getId(), dto.getName());
    }
}
