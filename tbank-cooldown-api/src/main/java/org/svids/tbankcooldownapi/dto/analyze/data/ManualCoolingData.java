package org.svids.tbankcooldownapi.dto.analyze.data;

import java.math.BigDecimal;

public record ManualCoolingData(BigDecimal minCost, BigDecimal maxCost) implements CoolingData {
}
