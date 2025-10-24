package ru.booking.management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ru.booking.common.models.RecommendResponse;
import ru.booking.common.models.RoomDto;
import ru.booking.management.service.RoomService;

import java.util.List;

@RestController
@Tag(name = "Работа с комнатами")
@RequestMapping("/rooms")
@Validated
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    @Operation(summary = "Создание комнаты")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomDto> createRoom(@RequestBody RoomDto roomDto) {
        return ResponseEntity.ok(roomService.createRoom(roomDto));
    }

    @Operation(summary = "Получение комнаты по id")
    @GetMapping("/recommend")
    public ResponseEntity<RecommendResponse> getRecommend() {
        return ResponseEntity.ok(roomService.getRecommended());
    }

    @Operation(summary = "Получение всех доступных комнат")
    @GetMapping
    public ResponseEntity<List<RoomDto>> getAvailable() {
        return ResponseEntity.ok(roomService.getAvailable());
    }

    @Operation(summary = "Подтверждение доступности комнаты")
    @PostMapping("/{id}/confirm-availability")
    public ResponseEntity<Boolean> confirmAvailability(@PathVariable("id") Long id) {
        return ResponseEntity.ok(roomService.confirmAvailability(id));
    }

    @Operation(summary = "Отмена подтверждения бронирования")
    @PostMapping("/{id}/release")
    public ResponseEntity<Void> releaseRoom(@PathVariable("id") Long id) {
        roomService.releaseRoom(id);
        return ResponseEntity.ok().build();
    }


}
