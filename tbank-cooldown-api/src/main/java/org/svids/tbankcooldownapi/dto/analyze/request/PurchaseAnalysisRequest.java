package org.svids.tbankcooldownapi.dto.analyze.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.svids.tbankcooldownapi.entity.PurchaseCategory;

import java.math.BigDecimal;

@Schema(description = "Запрос на анализ покупки")
public record PurchaseAnalysisRequest(
        @Schema(description = "Название товара", required = true, example = "iPhone 15")
        @NotBlank String name,

        @Schema(description = "Цена товара", required = true, example = "99999.99")
        @NotNull @Positive BigDecimal cost,

        @Schema(description = "Категория товара", required = true, example = "ELECTRONICS")
        @NotNull PurchaseCategory category
) {}
