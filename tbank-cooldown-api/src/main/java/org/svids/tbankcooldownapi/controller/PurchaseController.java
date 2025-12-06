package org.svids.tbankcooldownapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.svids.tbankcooldownapi.dto.AuthDto;
import org.svids.tbankcooldownapi.dto.purchase.*;
import org.svids.tbankcooldownapi.dto.purchase.wished.WishedPurchases;
import org.svids.tbankcooldownapi.dto.purchase.wished.WishedStatusRequest;
import org.svids.tbankcooldownapi.entity.Purchase;
import org.svids.tbankcooldownapi.entity.PurchaseStatus;
import org.svids.tbankcooldownapi.mapper.PurchaseMapper;
import org.svids.tbankcooldownapi.service.PurchaseService;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {
    private final PurchaseService purchaseService;
    private final PurchaseMapper purchaseMapper;

    // Список добавленных покупок
    @GetMapping
    public ResponseEntity<CompletedPurchases> fetchCompletedPurchases(@RequestHeader(value = AuthDto.AUTH_HEADER, defaultValue = "35cf7863-8110-46dd-a92d-99175ea9ed38") UUID userId) {
        return ResponseEntity.ok(new CompletedPurchases(
                purchaseMapper.toDtos(purchaseService.getPurchases(userId, Set.of(PurchaseStatus.PURCHASED, PurchaseStatus.CANCELLED)))
        ));
    }

    // Список желаемых покупок
    @GetMapping("/wished")
    public ResponseEntity<WishedPurchases> fetchWishedPurchases(@RequestHeader(value = AuthDto.AUTH_HEADER, defaultValue = "35cf7863-8110-46dd-a92d-99175ea9ed38") UUID userId) {
        return ResponseEntity.ok(new WishedPurchases(
                purchaseMapper.toWishedDtos(purchaseService.getPurchases(userId, Set.of(PurchaseStatus.WISHED)))
        ));
    }

    // Добавление новой покупки
    @PostMapping
    public ResponseEntity<PurchaseResult> addPurchase(@RequestHeader(value = AuthDto.AUTH_HEADER, defaultValue = "35cf7863-8110-46dd-a92d-99175ea9ed38") UUID userId,
                                                      @RequestBody PurchaseRequest request) {
        if (request.status().equals(PurchaseStatus.CANCELLED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Установить статус отмененной покупки можно только после добавления покупки");
        }
        return ResponseEntity.ok(
                new PurchaseResult(purchaseService.savePurchase(userId, purchaseMapper.toEntity(request))
        ));
    }

    // Отправка покупки на анализ
    @PostMapping("/analysis")
    public ResponseEntity<PurchaseAnalysisResult> analyzePurchase(@RequestHeader(value = AuthDto.AUTH_HEADER, defaultValue = "35cf7863-8110-46dd-a92d-99175ea9ed38") UUID userId,
                                                                  @RequestBody PurchaseAnalysisRequest request) {
        return ResponseEntity.ok(purchaseService.analyzePurchase(userId, request));
    }

    // Изменение статуса желаемой покупки
    @PutMapping("/wished/{purchaseId}/status")
    public ResponseEntity<?> changeWishedStatus(@RequestHeader(value = AuthDto.AUTH_HEADER, defaultValue = "35cf7863-8110-46dd-a92d-99175ea9ed38") UUID userId,
                                                @PathVariable UUID purchaseId,
                                                @RequestBody WishedStatusRequest statusRequest) {
        if (statusRequest.status() == PurchaseStatus.WISHED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Нельзя изменить статус желаемой покупки на такой же");
        }
        Purchase purchase = purchaseService.getPurchase(userId, purchaseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найдена 'желаемая' покупка с purchaseId " + purchaseId));

        if (!purchase.getStatus().equals(PurchaseStatus.WISHED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Статус покупки не равен 'желаемой'");
        }
        purchaseService.updatePurchaseStatus(purchase, statusRequest.status());
        return ResponseEntity.ok().build();
    }
}
