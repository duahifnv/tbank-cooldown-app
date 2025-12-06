package org.svids.tbankcooldownapi.dto.analyze;

import java.math.BigDecimal;

public record ManualCoolingDataDto(BigDecimal minCost, BigDecimal maxCost) implements CoolingDataDto {
}
