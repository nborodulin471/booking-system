package ru.booking.reserver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ru.booking.reserver.model.dto.booking.BookingDto;
import ru.booking.reserver.model.dto.booking.CreateBookingRequest;
import ru.booking.reserver.service.BookingService;

import java.util.List;

@RestController
@Validated
@Tag(name = "Работа с бронированием")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Operation(summary = "Бронирование")
    @PostMapping("/booking")
    public ResponseEntity<BookingDto> createBooking(@Valid @RequestBody CreateBookingRequest booking) {
        return ResponseEntity.ok(
                bookingService.createBooking(booking)
        );
    }

    @Operation(summary = "Получение списка бронирований")
    @GetMapping("/bookings")
    public ResponseEntity<List<BookingDto>> getBookings() {
        return ResponseEntity.ok(
                bookingService.getBookings()
        );
    }

    @Operation(summary = "Получение бронирования по id")
    @GetMapping("/booking/{id}")
    public ResponseEntity<BookingDto> getBooking(@PathVariable("id") long id) {
        return ResponseEntity.ok(
                bookingService.getBooking(id)
        );
    }

    @Operation(summary = "Отмена бронирования")
    @DeleteMapping("/booking/{id}")
    public void cancelBooking(@PathVariable("id") long id) {
        bookingService.cancelBooking(id);
        ResponseEntity.ok();
    }

}
