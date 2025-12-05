package org.svids.tbankcooldownapi.dto.purchase.wished;

import org.svids.tbankcooldownapi.entity.PurchaseCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record WishedPurchases(List<PurchaseDto> purchases) {

    public record PurchaseDto(UUID id,
                              String name,
                              BigDecimal price,
                              PurchaseCategory category,
                              LocalDate wishedDate,
                              int coolingTimeout) {
    }
}
