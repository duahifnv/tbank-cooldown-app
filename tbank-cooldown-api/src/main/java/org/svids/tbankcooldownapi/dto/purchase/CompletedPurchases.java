package org.svids.tbankcooldownapi.dto.purchase;

import io.swagger.v3.oas.annotations.media.Schema;
import org.svids.tbankcooldownapi.entity.PurchaseCategory;
import org.svids.tbankcooldownapi.entity.PurchaseStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Schema(description = "Список завершенных покупок")
public record CompletedPurchases(
        @Schema(description = "Список покупок")
        List<PurchaseDto> purchases
) {

    @Schema(description = "Данные о покупке")
    public record PurchaseDto(
            @Schema(description = "ID покупки", example = "550e8400-e29b-41d4-a716-446655440000")
            UUID id,

            @Schema(description = "Название товара", example = "Ноутбук")
            String name,

            @Schema(description = "Цена", example = "75000.00")
            BigDecimal cost,

            @Schema(description = "Категория", example = "ELECTRONICS")
            PurchaseCategory category,

            @Schema(description = "Дата покупки", example = "2024-01-15")
            LocalDate date,

            @Schema(description = "Статус покупки", example = "PURCHASED")
            PurchaseStatus status
    ) {}
}