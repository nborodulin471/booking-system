package ru.booking.common.models;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Комната")
public record RoomDto(
        @Schema(description = "Идентификатор комнаты")
        long id,
        @Schema(description = "Идентификатор отеля")
        long hotelId,
        @Schema(description = "Номер комнаты")
        int number,
        @Schema(description = "Доступность комнаты")
        boolean availability,
        @Schema(description = "Количество бронирований")
        long timesBooked
) {
}
