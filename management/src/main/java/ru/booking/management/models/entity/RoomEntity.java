package ru.booking.management.models.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "rooms")
public class RoomEntity {

    public RoomEntity() {
    }

    public RoomEntity(Long id, HotelEntity hotel, int number, boolean available, int timeBooked) {
        this.id = id;
        this.hotel = hotel;
        this.number = number;
        this.available = available;
        this.timeBooked = timeBooked;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hotel_id", referencedColumnName = "id")
    private HotelEntity hotel;

    @Column(name = "number")
    private int number;

    @Column(name = "available")
    private boolean available;

    @Column(name = "time_booked")
    private int timeBooked;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HotelEntity getHotel() {
        return hotel;
    }

    public void setHotel(HotelEntity hotel) {
        this.hotel = hotel;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }


    public int getTimeBooked() {
        return timeBooked;
    }

    public void setTimeBooked(int timeBooked) {
        this.timeBooked = timeBooked;
    }
}
