package org.svids.tbankcooldownapi.dto.analyze.data;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Настройки автоматического охлаждения")
public record AutoCoolingData(
        @Schema(description = "Месячный бюджет на свободные траты", example = "50000.00")
        BigDecimal monthBudget,

        @Schema(description = "Текущие накопления", example = "10000.00")
        BigDecimal totalSavings,

        @Schema(description = "Месячная зарплата", example = "100000.00")
        BigDecimal monthSalary
) implements CoolingData {}
