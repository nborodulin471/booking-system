package ru.booking.common.models;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Рекомендованные комнаты")
public record RecommendResponse(
        @Schema(description = "Комнаты")
        List<RoomDto> roomDtos
) {
}
