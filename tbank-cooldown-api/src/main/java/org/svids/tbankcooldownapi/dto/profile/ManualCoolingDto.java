package org.svids.tbankcooldownapi.dto.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Настройки ручного охлаждения")
public record ManualCoolingDto(
        @Schema(
                description = "Минимальная цена для срабатывания охлаждения",
                example = "5000.00",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Минимальная цена обязательна")
        @JsonProperty("minCost")
        BigDecimal minCost,

        @Schema(
                description = "Максимальная цена для срабатывания охлаждения",
                example = "50000.00",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Максимальная цена обязательна")
        @JsonProperty("maxCost")
        BigDecimal maxCost,

        @Schema(
                description = "Время охлаждения в секундах",
                example = "600",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Время охлаждения обязательно")
        @JsonProperty("coolingTimeout")
        Integer coolingTimeout
) {}