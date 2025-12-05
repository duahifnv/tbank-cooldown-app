package org.svids.tbankcooldownapi.dto.purchase;

import org.svids.tbankcooldownapi.entity.ItemCategoryType;
import org.svids.tbankcooldownapi.entity.StatusType;

import java.math.BigDecimal;

public record PurchaseRequest(String name, BigDecimal price, ItemCategoryType category, StatusType status) {
}
