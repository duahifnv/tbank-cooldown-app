package org.svids.tbankcooldownapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.svids.tbankcooldownapi.dto.purchase.wished.WishedPurchases;
import org.svids.tbankcooldownapi.dto.purchase.wished.WishedStatusRequest;
import org.svids.tbankcooldownapi.dto.purchase.*;
import org.svids.tbankcooldownapi.entity.PurchaseCategory;
import org.svids.tbankcooldownapi.entity.PurchaseStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {
    private final Random random = new Random();

    // Список подтвержденных и отмененные покупки
    @GetMapping
    public ResponseEntity<CompletedPurchases> fetchCompletedPurchases() {
        var purchaseDto = new CompletedPurchases.PurchaseDto(
                UUID.randomUUID(),
                "sample_purchase",
                BigDecimal.TEN,
                PurchaseCategory.ELECTRONICS,
                LocalDate.now(),
                PurchaseStatus.WISHED
        );
        return ResponseEntity.ok(new CompletedPurchases(List.of(purchaseDto)));
    }

    // Отправка покупки на анализ
    @PostMapping("/analysis")
    public ResponseEntity<PurchaseAnalysisResult> analyzePurchase(@RequestBody PurchaseAnalysisRequest request) {
        // здесь анализ и возврат результата
        return ResponseEntity.ok(new PurchaseAnalysisResult(random.nextBoolean(), random.nextInt(0, 100)));
    }

    // Добавление новой покупки
    @PostMapping
    public ResponseEntity<PurchaseResult> addPurchase(@RequestBody PurchaseRequest request) {
        return ResponseEntity.ok(new PurchaseResult(UUID.randomUUID()));
    }

    // Список желаемых покупок
    @GetMapping("/wished")
    public ResponseEntity<WishedPurchases> fetchWishedPurchases() {
        var purchaseDto = new WishedPurchases.PurchaseDto(
                UUID.randomUUID(),
                "sample_wished_purchase",
                BigDecimal.TEN,
                PurchaseCategory.ELECTRONICS,
                LocalDate.now(),
                random.nextInt(0, 10000)
        );
        return ResponseEntity.ok(new WishedPurchases(List.of(purchaseDto)));
    }

    // Изменение статуса желаемой покупки
    @PutMapping("/wished/{id}/status")
    public ResponseEntity<?> changeWishedStatus(@PathVariable Long id, @RequestBody WishedStatusRequest statusRequest) {
        if (statusRequest.status() == PurchaseStatus.WISHED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Нельзя изменить статус желаемой покупки на такой же");
        }
        return ResponseEntity.ok().build();
    }
}
