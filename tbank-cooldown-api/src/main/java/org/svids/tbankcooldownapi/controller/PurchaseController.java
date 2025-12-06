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
import org.svids.tbankcooldownapi.dto.analyze.data.PurchaseAnalysisResult;
import org.svids.tbankcooldownapi.dto.analyze.request.PurchaseAnalysisRequest;
import org.svids.tbankcooldownapi.dto.purchase.CompletedPurchases;
import org.svids.tbankcooldownapi.dto.purchase.PurchaseRequest;
import org.svids.tbankcooldownapi.dto.purchase.PurchaseResult;
import org.svids.tbankcooldownapi.dto.purchase.wished.WishedPurchases;
import org.svids.tbankcooldownapi.dto.purchase.wished.WishedStatusRequest;
import org.svids.tbankcooldownapi.entity.Purchase;
import org.svids.tbankcooldownapi.entity.PurchaseStatus;
import org.svids.tbankcooldownapi.entity.User;
import org.svids.tbankcooldownapi.mapper.PurchaseMapper;
import org.svids.tbankcooldownapi.service.PurchaseService;
import org.svids.tbankcooldownapi.service.UserService;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
@Tag(
        name = "Покупки",
        description = "Управление покупками и их анализом"
)
public class PurchaseController {
    private final PurchaseService purchaseService;
    private final PurchaseMapper purchaseMapper;
    private final UserService userService;

    @Operation(
            summary = "Получить историю покупок",
            description = "Возвращает список совершенных и отмененных покупок пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список покупок",
                            content = @Content(schema = @Schema(implementation = CompletedPurchases.class))
                    )
            }
    )
    @GetMapping
    public ResponseEntity<CompletedPurchases> fetchCompletedPurchases(
            @Parameter(
                    description = "UUID пользователя из заголовка X-USER-ID",
                    required = true,
                    example = "35cf7863-8110-46dd-a92d-99175ea9ed38"
            )
            @RequestHeader(value = AuthDto.AUTH_HEADER) UUID userId) {

        return ResponseEntity.ok(new CompletedPurchases(
                purchaseMapper.toDtos(purchaseService.getPurchases(userId,
                        Set.of(PurchaseStatus.PURCHASED, PurchaseStatus.CANCELLED)))
        ));
    }

    @Operation(
            summary = "Получить список желаемых покупок",
            description = """
            Возвращает список покупок со статусом WISHED.
            Для каждой покупки может быть указано оставшееся время охлаждения.
            Если coolingTimeout = null, значит охлаждение завершено.
            """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список желаемых покупок",
                            content = @Content(schema = @Schema(implementation = WishedPurchases.class))
                    )
            }
    )
    @GetMapping("/wished")
    public ResponseEntity<WishedPurchases> fetchWishedPurchases(
            @Parameter(
                    description = "UUID пользователя из заголовка X-USER-ID",
                    required = true,
                    example = "35cf7863-8110-46dd-a92d-99175ea9ed38"
            )
            @RequestHeader(value = AuthDto.AUTH_HEADER) UUID userId) {

        return ResponseEntity.ok(new WishedPurchases(
                purchaseMapper.toWishedDtos(purchaseService.getPurchases(userId,
                        Set.of(PurchaseStatus.WISHED)))
        ));
    }

    @Operation(
            summary = "Добавить новую покупку",
            description = """
            Добавляет покупку с указанным статусом.
            
            **Ограничения:**
            - Нельзя установить статус CANCELLED при создании
            - Только для статуса WISHED можно указать coolingTimeout
            """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Покупка успешно добавлена",
                            content = @Content(schema = @Schema(implementation = PurchaseResult.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректные параметры запроса"
                    )
            }
    )
    @PostMapping
    public ResponseEntity<PurchaseResult> addPurchase(
            @Parameter(
                    description = "UUID пользователя из заголовка X-USER-ID",
                    required = true,
                    example = "35cf7863-8110-46dd-a92d-99175ea9ed38"
            )
            @RequestHeader(value = AuthDto.AUTH_HEADER) UUID userId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные о покупке",
                    required = true
            )
            @RequestBody PurchaseRequest request) {

        if (request.status().equals(PurchaseStatus.CANCELLED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Установить статус отмененной покупки можно только после добавления покупки");
        }
        if (!request.status().equals(PurchaseStatus.WISHED) && request.coolingTimeout() != 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Невозможно задать охлаждение покупке без статуса 'желаемая'");
        }
        return ResponseEntity.ok(
                new PurchaseResult(purchaseService.savePurchase(userId,
                        purchaseMapper.toEntity(request)))
        );
    }

    @Operation(
            summary = "Анализ покупки на импульсивность",
            description = """
            Проверяет покупку на необходимость охлаждения.
            Возвращает результат анализа с рекомендациями.
            
            **Логика работы:**
            1. Проверяет настройки охлаждения пользователя (Auto/Manual)
            2. Рассчитывает необходимое время охлаждения
            3. Возвращает рекомендацию
            """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Результат анализа",
                            content = @Content(schema = @Schema(implementation = PurchaseAnalysisResult.class))
                    )
            }
    )
    @PostMapping("/analysis")
    public ResponseEntity<PurchaseAnalysisResult> analyzePurchase(
            @Parameter(
                    description = "UUID пользователя из заголовка X-USER-ID",
                    required = true,
                    example = "35cf7863-8110-46dd-a92d-99175ea9ed38"
            )
            @RequestHeader(value = AuthDto.AUTH_HEADER) UUID userId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные о покупке для анализа",
                    required = true
            )
            @RequestBody PurchaseAnalysisRequest request) {
        User user = userService.findById(userId).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Пользователь не найден"
        ));
        return ResponseEntity.ok(purchaseService.analyzePurchase(user, request));
    }

    @Operation(
            summary = "Изменить статус желаемой покупки",
            description = """
            Изменяет статус покупки с WISHED на PURCHASED или CANCELLED.
            Используется после завершения периода охлаждения.
            """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Статус успешно обновлен"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный запрос"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Покупка не найдена"
                    )
            }
    )
    @PutMapping("/wished/{purchaseId}/status")
    public ResponseEntity<?> changeWishedStatus(
            @Parameter(
                    description = "UUID пользователя из заголовка X-USER-ID",
                    required = true,
                    example = "35cf7863-8110-46dd-a92d-99175ea9ed38"
            )
            @RequestHeader(value = AuthDto.AUTH_HEADER) UUID userId,

            @Parameter(
                    description = "ID желаемой покупки",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID purchaseId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Новый статус покупки",
                    required = true
            )
            @RequestBody WishedStatusRequest statusRequest) {

        if (statusRequest.status() == PurchaseStatus.WISHED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Нельзя изменить статус желаемой покупки на такой же");
        }
        Purchase purchase = purchaseService.getPurchase(userId, purchaseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Не найдена 'желаемая' покупка с purchaseId " + purchaseId));

        if (!purchase.getStatus().equals(PurchaseStatus.WISHED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Статус покупки не равен 'желаемой'");
        }
        purchaseService.updatePurchaseStatus(purchase, statusRequest.status());
        return ResponseEntity.ok().build();
    }
}

