package org.svids.tbankcooldownapi.dto.analyze.request;

import java.math.BigDecimal;

public record AutoCoolingRequest(BigDecimal monthBudget, BigDecimal totalSavings, BigDecimal monthSalary) {
}
