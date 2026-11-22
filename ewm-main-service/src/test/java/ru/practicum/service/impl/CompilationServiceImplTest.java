package ru.practicum.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Compilation;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompilationServiceImplTest {

    @Mock
    private CompilationRepository compilationRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private StatsClient statsClient;

    @InjectMocks
    private CompilationServiceImpl compilationService;

    @Test
    void createCompilation_shouldSaveWithoutEvents() {
        NewCompilationDto dto = new NewCompilationDto();
        dto.setTitle("Летние концерты");
        dto.setPinned(true);

        when(compilationRepository.save(any())).thenAnswer(inv -> {
            Compilation c = inv.getArgument(0);
            c.setId(1L);
            return c;
        });

        CompilationDto result = compilationService.createCompilation(dto);

        assertEquals("Летние концерты", result.getTitle());
        assertTrue(result.getPinned());
    }

    @Test
    void getCompilationById_shouldThrowNotFound() {
        when(compilationRepository.findByIdWithEvents(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> compilationService.getCompilationById(99L));
    }

    @Test
    void deleteCompilation_shouldDeleteExisting() {
        when(compilationRepository.existsById(1L)).thenReturn(true);

        compilationService.deleteCompilation(1L);

        verify(compilationRepository).deleteById(1L);
    }
}