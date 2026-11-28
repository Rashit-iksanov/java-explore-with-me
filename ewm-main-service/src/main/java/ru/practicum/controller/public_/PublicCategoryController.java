package ru.practicum.controller.public_;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.CategoryDto;
import ru.practicum.service.CategoryService;

import java.util.List;

/**
 * Публичный API для работы с категориями
 */
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class PublicCategoryController {

    private final CategoryService categoryService;

    /**
     * Получение категорий.
     * В случае, если по заданным фильтрам не найдено ни одной категории, возвращает пустой список
     *
     * @param from количество категорий, которые нужно пропустить для формирования текущего набора (по умолчанию: 0)
     * @param size количество категорий в наборе (по умолчанию: 10)
     * @return список обьектов CategoryDto
     */
    @GetMapping
    public List<CategoryDto> getCategories(
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        return categoryService.getCategories(from, size);
    }

    /**
     * Получение информации о категории по её идентификатору
     * В случае, если категории с заданным id не найдено, возвращает статус код 404
     *
     * @param catId id категории
     * @return Категория присвоена.
     * @throws ru.practicum.exception.NotFoundException если категория не найдена
     */
    @GetMapping("/{catId}")
    public CategoryDto getCategory(@PathVariable Long catId) {
        return categoryService.getCategoryById(catId);
    }
}