package ru.practicum.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_shouldSaveUserWhenEmailIsUnique() {
        NewUserRequest request = new NewUserRequest("Иван", "ivan@mail.ru");
        User savedUser = new User(1L, "Иван", "ivan@mail.ru");

        when(userRepository.existsByEmail("ivan@mail.ru")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDto result = userService.createUser(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Иван", result.getName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_shouldThrowConflictWhenEmailExists() {
        NewUserRequest request = new NewUserRequest("Иван", "ivan@mail.ru");
        when(userRepository.existsByEmail("ivan@mail.ru")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUsers_shouldReturnUsersByIds() {
        List<Long> ids = List.of(1L, 2L);
        List<User> users = List.of(
                new User(1L, "Иван", "ivan@mail.ru"),
                new User(2L, "Петр", "petr@mail.ru"));

        when(userRepository.findByIds(eq(ids), any(Pageable.class))).thenReturn(users);

        List<UserDto> result = userService.getUsers(ids, 0, 10);

        assertEquals(2, result.size());
        verify(userRepository).findByIds(eq(ids), any(Pageable.class));
        verify(userRepository, never()).findAllUsers(any());
    }

    @Test
    void deleteUser_shouldDeleteExistingUser() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_shouldThrowNotFoundWhenUserAbsent() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService.deleteUser(99L));
        verify(userRepository, never()).deleteById(any());
    }
}