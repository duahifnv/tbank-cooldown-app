package org.svids.tbankcooldownapi.dto.purchase.wished;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.svids.tbankcooldownapi.entity.PurchaseStatus;

@Schema(description = "Запрос на изменение статуса желаемой покупки")
public record WishedStatusRequest(
        @Schema(description = "Новый статус покупки", required = true, example = "PURCHASED")
        @NotNull PurchaseStatus status
) {}
