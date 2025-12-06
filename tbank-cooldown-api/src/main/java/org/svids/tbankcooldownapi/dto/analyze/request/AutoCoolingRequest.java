package org.svids.tbankcooldownapi.dto.analyze.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Запрос на установку автоматического охлаждения")
public record AutoCoolingRequest(
        @Schema(description = "Месячный бюджет на свободные траты", required = true, example = "50000.00")
        @NotNull BigDecimal monthBudget,

        @Schema(description = "Текущие накопления", required = true, example = "10000.00")
        @NotNull BigDecimal totalSavings,

        @Schema(description = "Месячная зарплата", required = true, example = "100000.00")
        @NotNull BigDecimal monthSalary
) {}