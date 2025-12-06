package org.svids.tbankcooldownapi.dto.purchase;

import org.svids.tbankcooldownapi.entity.PurchaseCategory;

import java.math.BigDecimal;

public record PurchaseAnalysisRequest(String name, BigDecimal cost, PurchaseCategory category) {
}
