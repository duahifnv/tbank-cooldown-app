package org.svids.tbankcooldownapi.dto.analyze.data;

public sealed interface CoolingData permits AutoCoolingData, ManualCoolingData {
}
