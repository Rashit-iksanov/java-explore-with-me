package ru.practicum.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.service.CategoryService;
import ru.practicum.validation.OnCreate;

/**
 * API для управления категориями событий.
 */
@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    /**
     * Добавление новой категории.
     * Имя категории должно быть уникальным.
     *
     * @param request данные добавляемой категории
     * @return объект CategoryDto с присвоенным идентификатором
     * @throws ru.practicum.exception.ConflictException если категория с таким именем уже существует
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@Validated(OnCreate.class) @RequestBody NewCategoryDto request) {
        return categoryService.createCategory(request);
    }

    /**
     * Изменение категории.
     * Имя категории должно оставаться уникальным.
     *
     * @param catId id категории
     * @param dto   данные категории для изменения
     * @return обновленный объект CategoryDto
     * @throws ru.practicum.exception.NotFoundException если категория не найдена
     * @throws ru.practicum.exception.ConflictException если новое имя категории уже занято
     */
    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@PathVariable Long catId,
                                      @Valid @RequestBody CategoryDto dto) {
        return categoryService.updateCategory(catId, dto);
    }

    /**
     * Удаление категории.
     * С категорией не должно быть связано ни одного события.
     *
     * @param catId id категории
     * @throws ru.practicum.exception.NotFoundException если категория не найдена
     * @throws ru.practicum.exception.ConflictException если категория не пуста (связана с событиями)
     */
    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        categoryService.deleteCategory(catId);
    }
}