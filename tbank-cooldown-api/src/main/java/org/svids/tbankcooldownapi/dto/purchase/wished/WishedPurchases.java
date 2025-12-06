package org.svids.tbankcooldownapi.dto.purchase.wished;

import io.swagger.v3.oas.annotations.media.Schema;
import org.svids.tbankcooldownapi.entity.PurchaseCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Schema(description = "Список желаемых покупок")
public record WishedPurchases(
        @Schema(description = "Список желаемых покупок")
        List<PurchaseDto> purchases
) {

    @Schema(description = "Данные о желаемой покупке")
    public record PurchaseDto(
            @Schema(description = "ID покупки", example = "550e8400-e29b-41d4-a716-446655440000")
            UUID id,

            @Schema(description = "Название товара", example = "Смартфон")
            String name,

            @Schema(description = "Цена", example = "50000.00")
            BigDecimal cost,

            @Schema(description = "Категория", example = "ELECTRONICS")
            PurchaseCategory category,

            @Schema(description = "Дата добавления в желаемое", example = "2024-01-15")
            LocalDate wishedDate,

            @Schema(description = "Оставшееся время охлаждения в секундах (null если завершено)",
                    example = "1800", nullable = true)
            Integer coolingTimeout
    ) {}
}
