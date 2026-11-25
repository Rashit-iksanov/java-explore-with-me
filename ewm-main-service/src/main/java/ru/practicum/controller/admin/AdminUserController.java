package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;
import ru.practicum.service.UserService;
import ru.practicum.validation.OnCreate;

import java.util.List;

/**
 * API для управления пользователями.
 * Позволяет создавать, просматривать и удалять пользователей системы.
 */
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    /**
     * Добавление нового пользователя.
     *
     * @param request данные добавляемого пользователя (имя и email)
     * @return объект UserDto с присвоенным идентификатором
     * @throws ru.practicum.exception.ConflictException если пользователь с таким email уже существует
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Validated(OnCreate.class) @RequestBody NewUserRequest request) {
        return userService.createUser(request);
    }

    /**
     * Получение информации о пользователях.
     * Возвращает информацию обо всех пользователях или о конкретных по указанным идентификаторам.
     *
     * @param ids  список id пользователей
     * @param from количество элементов, которые нужно пропустить для формирования текущего набора (по умолчанию 0)
     * @param size количество элементов в наборе (по умолчанию 10)
     * @return список объектов UserDto
     */
    @GetMapping
    public List<UserDto> getUsers(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        return userService.getUsers(ids, from, size);
    }

    /**
     * Удаление пользователя.
     *
     * @param userId id пользователя
     * @throws ru.practicum.exception.NotFoundException если пользователь не найден
     */
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}