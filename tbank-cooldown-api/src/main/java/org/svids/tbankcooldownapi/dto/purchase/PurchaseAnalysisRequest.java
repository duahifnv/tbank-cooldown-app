package org.svids.tbankcooldownapi.dto.purchase;

import org.svids.tbankcooldownapi.entity.ItemCategoryType;

import java.math.BigDecimal;

public record PurchaseAnalysisRequest(String name, BigDecimal price, ItemCategoryType category) {
}
