package org.svids.tbankcooldownapi.dto.purchase;

import org.svids.tbankcooldownapi.entity.PurchaseCategory;
import org.svids.tbankcooldownapi.entity.PurchaseStatus;

import java.math.BigDecimal;

public record PurchaseRequest(String name,
                              BigDecimal cost,
                              PurchaseCategory category,
                              PurchaseStatus status) {
}
