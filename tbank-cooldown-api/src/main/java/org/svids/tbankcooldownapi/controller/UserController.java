package org.svids.tbankcooldownapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.svids.tbankcooldownapi.dto.AuthDto;
import org.svids.tbankcooldownapi.dto.analyze.request.AutoCoolingRequest;
import org.svids.tbankcooldownapi.dto.analyze.request.ManualCoolingRequest;
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

    @Operation(
            summary = "Установить ручной режим охлаждения",
            description = """
            Настройка ручного режима охлаждения покупок.
            В этом режиме охлаждение применяется к покупкам,
            цена которых попадает в указанный диапазон.
            """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Настройки успешно обновлены"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Пользователь не найден"
                    )
            }
    )
    @PutMapping("/cooling/mode/manual")
    public ResponseEntity<?> setManualCooling(
            @Parameter(
                    description = "UUID пользователя из заголовка X-USER-ID",
                    required = true,
                    example = "35cf7863-8110-46dd-a92d-99175ea9ed38"
            )
            @RequestHeader(value = AuthDto.AUTH_HEADER) UUID userId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Настройки ручного охлаждения",
                    required = true
            )
            @RequestBody ManualCoolingRequest manualCoolingRequest) {

        User user = userService.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        userService.setManualCooling(user, manualCoolingRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Установить автоматический режим охлаждения",
            description = """
            Настройка автоматического режима охлаждения покупок.
            В этом режиме время охлаждения рассчитывается автоматически
            на основе бюджета, накоплений и зарплаты пользователя.
            """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Настройки успешно обновлены"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Пользователь не найден"
                    )
            }
    )
    @PutMapping("/cooling/mode/auto")
    public ResponseEntity<?> setAutoCooling(
            @Parameter(
                    description = "UUID пользователя из заголовка X-USER-ID",
                    required = true,
                    example = "35cf7863-8110-46dd-a92d-99175ea9ed38"
            )
            @RequestHeader(value = AuthDto.AUTH_HEADER) UUID userId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Настройки автоматического охлаждения",
                    required = true
            )
            @RequestBody AutoCoolingRequest autoCoolingRequest) {

        User user = userService.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        userService.setAutoCooling(user, autoCoolingRequest);
        return ResponseEntity.ok().build();
    }
}
