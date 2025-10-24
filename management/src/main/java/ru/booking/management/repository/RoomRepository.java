package ru.booking.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ru.booking.management.models.entity.RoomEntity;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Long> {

    @Query("select r from RoomEntity r where r.available = true order by r.timeBooked asc, r.id asc")
    List<RoomEntity> findRecommendedRooms();

    @Query("select r from RoomEntity r where r.available = true")
    List<RoomEntity> findAvailableRooms();

    boolean existsByNumber(int number);
}
