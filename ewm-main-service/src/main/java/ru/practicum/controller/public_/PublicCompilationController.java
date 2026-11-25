package ru.practicum.controller.public_;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.CompilationDto;
import ru.practicum.service.CompilationService;

import java.util.List;

/**
 * Публичный API для работы с подборками событий.
 */
@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class PublicCompilationController {

    private final CompilationService compilationService;

    /**
     * Получение подборок событий.
     * В случае, если по заданным фильтрам не найдено ни одной подборки, возвращает пустой список
     *
     * @param pinned искать только закрепленные/не закрепленные подборки
     * @param from   количество элементов, которые нужно пропустить для формирования текущего набора (по умолчанию: 0)
     * @param size   количество элементов в наборе (по умолчанию: 10)
     * @return список подборок событий.
     */
    @GetMapping
    public List<CompilationDto> getCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        return compilationService.getCompilations(pinned, from, size);
    }

    /**
     * Получение подборки событий по его id.
     * В случае, если подборки с заданным id не найдено, возвращает статус код 404.
     *
     * @param compId id подборки
     * @return объект CompilationDto
     * @throws ru.practicum.exception.NotFoundException если подборка не найдена
     */
    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@PathVariable Long compId) {
        return compilationService.getCompilationById(compId);
    }
}