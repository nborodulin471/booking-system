package ru.booking.reserver.model.dto.booking;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Запрос на создание бронирования")
public record CreateBookingRequest(
        @Schema(description = "Данные бронирования")
        @NotNull
        BookingDto booking,
        @Schema(description = "Автоматически выбрать комнату")
        boolean autoSelect
) {
}
