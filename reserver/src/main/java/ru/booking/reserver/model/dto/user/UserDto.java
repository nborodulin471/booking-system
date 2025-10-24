package ru.booking.reserver.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import ru.booking.reserver.model.Role;

@Schema(description = "Данные пользователя")
public record UserDto(
        @Schema(description = "Идентификатор пользователя", example = "1")
        long id,

        @Schema(description = "Имя пользователя", example = "Jon")
        @Size(min = 5, max = 50, message = "Имя пользователя должно содержать от 5 до 50 символов")
        @NotBlank(message = "Имя пользователя не может быть пустыми")
        String name,

        @Schema(description = "Роль пользователя", example = "ADMIN")
        Role role
) {
}
