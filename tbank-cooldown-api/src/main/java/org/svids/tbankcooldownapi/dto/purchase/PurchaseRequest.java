package org.svids.tbankcooldownapi.dto.purchase;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.svids.tbankcooldownapi.entity.PurchaseCategory;
import org.svids.tbankcooldownapi.entity.PurchaseStatus;

import java.math.BigDecimal;

@Schema(description = "Запрос на создание покупки")
public record PurchaseRequest(
        @Schema(description = "Название товара", required = true, example = "Кроссовки")
        @NotBlank String name,

        @Schema(description = "Цена товара", required = true, example = "15000.00")
        @NotNull @Positive BigDecimal cost,

        @Schema(description = "Категория товара", required = true, example = "CLOTHING")
        @NotNull PurchaseCategory category,

        @Schema(description = "Статус покупки", required = true, example = "WISHED")
        @NotNull PurchaseStatus status,

        @Schema(description = "Время охлаждения в секундах (только для статуса WISHED)",
                example = "300", defaultValue = "0")
        @Min(0) int coolingTimeout
) {}