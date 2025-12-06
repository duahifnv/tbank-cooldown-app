package org.svids.tbankcooldownapi.dto.analyze.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Запрос на установку ручного охлаждения")
public record ManualCoolingRequest(
        @Schema(description = "Минимальная цена для срабатывания охлаждения", required = true, example = "5000.00")
        @NotNull BigDecimal minCost,

        @Schema(description = "Максимальная цена для срабатывания охлаждения", required = true, example = "50000.00")
        @NotNull BigDecimal maxCost,

        @Schema(description = "Время охлаждения в секундах", required = true, example = "600")
        @NotNull Integer coolingTimeout
) {}