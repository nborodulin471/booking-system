package ru.booking.reserver.model.dto.booking;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import ru.booking.reserver.model.BookingStatus;

import java.time.LocalDateTime;

@Schema(description = "Бронирование")
public record BookingDto(
        @Schema(description = "Идентификатор бронирования", example = "123")
        @Min(0)
        long id,

        @Schema(description = "Идентификатор комнаты", example = "123")
        @Min(0)
        long roomId,

        @Schema(description = "Дата начала бронирования")
        @NotNull(message = "Дата начала обязательна")
        LocalDateTime dateStart,

        @Schema(description = "Дата начала окончания")
        @NotNull(message = "Дата окончания обязательна")
        LocalDateTime dateEnd,

        @Schema(description = "Статус бронирования")
        BookingStatus status
) {
}
