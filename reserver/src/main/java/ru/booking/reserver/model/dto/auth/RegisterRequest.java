package ru.booking.reserver.model.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на регистрацию")
public record RegisterRequest(
        @Schema(description = "Имя пользователя", example = "Jon")
        @Size(max = 255, message = "Имя пользователя должно содержать от 5 до 50 символов")
        @NotBlank(message = "Имя пользователя не может быть пустыми")
        String username,

        @Schema(description = "Пароль", example = "my_1secret1_password")
        @Size(max = 255, message = "Длина пароля должна быть не более 255 символов")
        String password
) {
}
