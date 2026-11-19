package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Request;
import ru.practicum.model.enums.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    // Поиск заявок по пользователю
    @Query("SELECT r FROM Request r WHERE r.requester.id = :requesterId")
    List<Request> findByRequesterId(@Param("requesterId") Long requesterId);

    // Поиск заявок по событию
    @Query("SELECT r FROM Request r WHERE r.event.id = :eventId")
    List<Request> findByEventId(@Param("eventId") Long eventId);

    // Поиск заявок по событию и статусу
    @Query("SELECT r FROM Request r WHERE r.event.id = :eventId AND r.status = :status")
    List<Request> findByEventIdAndStatus(@Param("eventId") Long eventId, @Param("status") RequestStatus status);

    // Подсчет подтвержденных заявок по списку событий
    @Query("SELECT r.event.id, COUNT(r) FROM Request r " +
            "WHERE r.event.id IN :eventIds AND r.status = 'CONFIRMED' " +
            "GROUP BY r.event.id")
    List<Object[]> countConfirmedByEventIds(@Param("eventIds") List<Long> eventIds);

    // Подсчет подтвержденных заявок для одного события
    @Query("SELECT COUNT(r) FROM Request r WHERE r.event.id = :eventId AND r.status = 'CONFIRMED'")
    Long countConfirmedByEventId(@Param("eventId") Long eventId);

    // Проверка существования заявки
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Request r " +
            "WHERE r.requester.id = :requesterId AND r.event.id = :eventId")
    boolean existsByRequesterIdAndEventId(@Param("requesterId") Long requesterId, @Param("eventId") Long eventId);

    // Поиск конкретной заявки
    @Query("SELECT r FROM Request r WHERE r.id = :id AND r.requester.id = :requesterId")
    Optional<Request> findByIdAndRequesterId(@Param("id") Long id, @Param("requesterId") Long requesterId);

    // Поиск заявок по списку ID
    @Query("SELECT r FROM Request r WHERE r.id IN :ids")
    List<Request> findByIds(@Param("ids") List<Long> ids);
}
