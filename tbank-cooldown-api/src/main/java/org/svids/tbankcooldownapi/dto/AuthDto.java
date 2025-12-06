package org.svids.tbankcooldownapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Результат аутентификации")
public record AuthDto(
        @Schema(description = "ID пользователя для заголовка X-USER-ID",
                example = "35cf7863-8110-46dd-a92d-99175ea9ed38")
        UUID id
) {
    public static final String AUTH_HEADER = "X-USER-ID";
}
