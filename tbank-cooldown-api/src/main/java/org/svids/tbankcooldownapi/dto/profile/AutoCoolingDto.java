package org.svids.tbankcooldownapi.dto.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Настройки автоматического охлаждения")
public record AutoCoolingDto(
        @Schema(
                description = "Месячный бюджет на свободные траты",
                example = "50000.00",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Месячный бюджет обязателен")
        @JsonProperty("monthBudget")
        BigDecimal monthBudget,

        @Schema(
                description = "Текущие накопления",
                example = "10000.00",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Текущие накопления обязательны")
        @JsonProperty("totalSavings")
        BigDecimal totalSavings,

        @Schema(
                description = "Месячная зарплата",
                example = "100000.00",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Месячная зарплата обязательна")
        @JsonProperty("monthSalary")
        BigDecimal monthSalary
) {}