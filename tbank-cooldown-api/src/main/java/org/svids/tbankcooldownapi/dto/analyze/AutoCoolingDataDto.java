package org.svids.tbankcooldownapi.dto.analyze;

import java.math.BigDecimal;

public record AutoCoolingDataDto(BigDecimal monthBudget, BigDecimal totalSavings, BigDecimal monthSalary) implements CoolingDataDto {
}
