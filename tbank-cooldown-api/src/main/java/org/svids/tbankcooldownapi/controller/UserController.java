package org.svids.tbankcooldownapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.svids.tbankcooldownapi.dto.AuthDto;
import org.svids.tbankcooldownapi.dto.profile.UserProfileDto;
import org.svids.tbankcooldownapi.entity.User;
import org.svids.tbankcooldownapi.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(
        name = "Пользователи",
        description = "Управление пользователями и настройками охлаждения"
)
public class UserController {
    public final UserService userService;

    @Operation(
            summary = "Аутентификация пользователя",
            description = """
            Создает нового пользователя.
            Возвращает UUID для использования в заголовке X-USER-ID.
            Этот эндпоинт НЕ требует заголовка X-USER-ID.
            """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешная аутентификация",
                            content = @Content(schema = @Schema(implementation = AuthDto.class))
                    )
            }
    )
    @PostMapping("/authenticate")
    public ResponseEntity<AuthDto> authenticate() {
        return ResponseEntity.ok(new AuthDto(userService.authenticateUser()));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getUserProfile(@Parameter(
            name = "X-USER-ID",
            description = """
                    UUID пользователя, полученный при аутентификации.
                    Должен быть передан в заголовке запроса.
                    
                    **Пример:** `35cf7863-8110-46dd-a92d-99175ea9ed38`
                    
                    ### Как получить:
                    1. Вызовите `POST /api/user/authenticate`
                    2. Сохраните полученный `id`
                    3. Используйте его во всех последующих запросах
                    """,
            required = true,
            example = "35cf7863-8110-46dd-a92d-99175ea9ed38",
            in = ParameterIn.HEADER
    ) @RequestHeader(value = AuthDto.AUTH_HEADER) UUID userId) {
        return ResponseEntity.ok(userService.findProfileById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Пользователь не найден"
                )));
    }

    @Operation(
            summary = "Обновление профиля пользователя",
            description = """
            Обновляет профиль пользователя, включая:
            
            ### Основная информация:
            - Никнейм
            - Информация о себе
            
            ### Настройки охлаждения:
            - Режим охлаждения (автоматический/ручной)
            - Запрещенные категории покупок
            - Параметры автоматического охлаждения (бюджет, накопления, зарплата)
            - Параметры ручного охлаждения (диапазон цен, время ожидания)
            
            ### Важные примечания:
            1. При включении autoCoolingEnabled=true:
               - Используются настройки из `autoCooling`
               - Настройки `manualCooling` игнорируются
            2. При autoCoolingEnabled=false:
               - Используются настройки из `manualCooling`
               - Настройки `autoCooling` игнорируются
            3. Запрещенные категории (bannedCategories):
               - Покупки из этих категорий всегда требуют охлаждения
               - Возможные значения: `ELECTRONICS`, `CLOTHING`, `FOOD`, `HOME`, `OTHER`
            
            ### Примеры использования:
            - Включить автоматическое охлаждение с бюджетом 50к
            - Запретить категорию ELECTRONICS
            - Установить ручное охлаждение для покупок от 5к до 50к
            """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для обновления профиля",
                    required = true
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Профиль успешно обновлен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = @ExampleObject(
                                            name = "Успех",
                                            value = "{}"
                                    )
                            )
                    )
            }
    )
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @Parameter(
                    description = "UUID пользователя из заголовка X-USER-ID",
                    required = true,
                    example = "35cf7863-8110-46dd-a92d-99175ea9ed38"
            )
            @RequestHeader(value = AuthDto.AUTH_HEADER) UUID userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для обновления профиля пользователя",
                    required = true
            )
            @RequestBody UserProfileDto request) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Пользователь не найден"
                ));
        userService.updateProfile(user, request);
        return ResponseEntity.ok().build();
    }
}
