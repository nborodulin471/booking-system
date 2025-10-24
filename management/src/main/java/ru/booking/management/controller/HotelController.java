package ru.booking.management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ru.booking.management.models.dto.HotelDto;
import ru.booking.management.service.HotelService;

import java.util.List;

@RestController
@Tag(name = "Работа с отелями")
@RequestMapping("/hotels")
@Validated
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @Operation(summary = "Создание отеля")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<HotelDto> addHotel(@Valid @RequestBody HotelDto hotel) {
        return ResponseEntity.ok(
                hotelService.create(hotel)
        );
    }

    @Operation(summary = "Получение всех отелей")
    @GetMapping
    public ResponseEntity<List<HotelDto>> findAll() {
        return ResponseEntity.ok(
                hotelService.findAll()
        );
    }

}
