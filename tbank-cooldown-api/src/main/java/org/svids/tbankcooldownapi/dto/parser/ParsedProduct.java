package org.svids.tbankcooldownapi.dto.parser;

import io.swagger.v3.oas.annotations.media.Schema;
import org.svids.tbankcooldownapi.entity.PurchaseCategory;

import java.math.BigDecimal;

@Schema(description = "Результат парсинга товара")
public record ParsedProduct(
        @Schema(description = "Название товара", example = "Смартфон Apple iPhone 15")
        String name,

        @Schema(description = "Цена товара", example = "89999.99")
        BigDecimal price,

        @Schema(description = "Категория товара", example = "ELECTRONICS")
        PurchaseCategory category,

        @Schema(description = "Магазин", example = "Wildberries")
        String store
) {
}
