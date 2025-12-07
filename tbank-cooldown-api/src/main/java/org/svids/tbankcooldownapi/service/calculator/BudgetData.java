package org.svids.tbankcooldownapi.service.calculator;

import java.math.BigDecimal;

public record BudgetData(BigDecimal monthBudget, BigDecimal totalSavings, BigDecimal monthSalary) {
}
