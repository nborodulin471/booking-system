package ru.booking.reserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.booking.reserver.model.entity.BookingEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    List<BookingEntity> findAllByUserId(Long userId);

    List<BookingEntity> findByRoomIdAndDateStartLessThanEqualAndDateEndGreaterThanEqual(
            long roomId, LocalDateTime newEnd, LocalDateTime newStart
    );

    default List<BookingEntity> findByRoomIdAndDateRange(long roomId, LocalDateTime newStart, LocalDateTime newEnd) {
        return findByRoomIdAndDateStartLessThanEqualAndDateEndGreaterThanEqual(roomId, newEnd, newStart);
    }

}
