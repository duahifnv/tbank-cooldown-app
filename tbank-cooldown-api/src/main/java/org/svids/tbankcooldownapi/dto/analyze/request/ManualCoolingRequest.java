package org.svids.tbankcooldownapi.dto.analyze.request;

import java.math.BigDecimal;

public record ManualCoolingRequest(BigDecimal minCost, BigDecimal maxCost, Integer coolingTimeout) {
}
