package org.svids.tbankcooldownapi.dto.purchase;

import org.svids.tbankcooldownapi.entity.PurchaseCategory;
import org.svids.tbankcooldownapi.entity.PurchaseStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CompletedPurchases(List<PurchaseDto> purchases) {
    public record PurchaseDto(UUID id, String name, BigDecimal price, PurchaseCategory category, LocalDate date, PurchaseStatus status) {
    }
}
