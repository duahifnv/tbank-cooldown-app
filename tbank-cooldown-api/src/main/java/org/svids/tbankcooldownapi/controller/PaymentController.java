package org.svids.tbankcooldownapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.svids.tbankcooldownapi.dto.purchase.PurchaseAnalysisRequest;
import org.svids.tbankcooldownapi.dto.purchase.PurchaseAnalysisResult;
import org.svids.tbankcooldownapi.dto.purchase.PurchaseRequest;
import org.svids.tbankcooldownapi.dto.purchase.PurchaseResult;

import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final Random random = new Random();

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
