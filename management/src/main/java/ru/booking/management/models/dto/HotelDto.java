package ru.booking.management.models.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Отель")
public record HotelDto(
        @Schema(description = "Идентификатор", example = "1")
        Long id,
        @Schema(description = "Название", example = "Отель")
        @NotNull
        String name,
        @Schema(description = "Адрес", example = "Адрес")
        @NotNull
        String address
) {
}
