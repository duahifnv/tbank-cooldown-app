package org.svids.tbankcooldownapi.dto.purchase;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Результат создания покупки")
public record PurchaseResult(
        @Schema(description = "ID созданной покупки", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID purchaseId
) {}

