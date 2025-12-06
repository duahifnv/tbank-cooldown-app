package org.svids.tbankcooldownapi.dto.analyze.data;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Настройки ручного охлаждения")
public record ManualCoolingData(
        @Schema(description = "Минимальная цена для срабатывания охлаждения", example = "5000.00")
        BigDecimal minCost,

        @Schema(description = "Максимальная цена для срабатывания охлаждения", example = "50000.00")
        BigDecimal maxCost
) implements CoolingData {}
