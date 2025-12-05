package org.svids.tbankcooldownapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping
    public ResponseEntity<PurchaseHistory> fetchHistory(UUID userId) {
        var purchaseDto = new PurchaseHistory.PurchaseDto(
                UUID.randomUUID(),
                "sample_purchase",
                BigDecimal.TEN,
                PurchaseCategory.ELECTRONICS,
                LocalDate.now(),
                PurchaseStatus.WISHED
        );
        return ResponseEntity.ok(new PurchaseHistory(List.of(purchaseDto)));
    }

    @PostMapping("/analysis")
    public ResponseEntity<PurchaseAnalysisResult> analyzePurchase(@RequestHeader("X-USER-ID") UUID userId,
                                                                  @RequestBody PurchaseAnalysisRequest request) {
        // здесь анализ и возврат результата
        return ResponseEntity.ok(new PurchaseAnalysisResult(random.nextBoolean(), random.nextInt(0, 100)));
    }

    @PostMapping
    public ResponseEntity<PurchaseResult> addPurchase(@RequestHeader("X-USER-ID") UUID userId,
                                                      @RequestBody PurchaseRequest request) {
        return ResponseEntity.ok(new PurchaseResult(UUID.randomUUID()));
    }
}
