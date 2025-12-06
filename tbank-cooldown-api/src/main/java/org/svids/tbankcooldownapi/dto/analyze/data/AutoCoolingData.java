package org.svids.tbankcooldownapi.dto.analyze.data;

import java.math.BigDecimal;

public record AutoCoolingData(BigDecimal monthBudget, BigDecimal totalSavings, BigDecimal monthSalary) implements CoolingData {
}
