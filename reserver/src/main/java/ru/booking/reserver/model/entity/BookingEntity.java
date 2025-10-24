package ru.booking.reserver.model.entity;

import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import ru.booking.reserver.model.BookingStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class BookingEntity {

    public BookingEntity() {
    }

    public BookingEntity(long id, long roomId, UserEntity user, LocalDateTime dateStart,
                         LocalDateTime dateEnd, BookingStatus status) {
        this.id = id;
        this.roomId = roomId;
        this.user = user;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.status = status;
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(name = "room_id")
    public long roomId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    public UserEntity user;

    @Column(name = "date_start")
    public LocalDateTime dateStart;

    @Column(name = "date_end")
    public LocalDateTime dateEnd;

    @Column(name = "status")
    public BookingStatus status;

    @Column(name = "created_at")
    @CreatedDate
    public LocalDateTime createdAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public LocalDateTime getDateStart() {
        return dateStart;
    }

    public void setDateStart(LocalDateTime dateStart) {
        this.dateStart = dateStart;
    }

    public LocalDateTime getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(LocalDateTime dateEnd) {
        this.dateEnd = dateEnd;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
