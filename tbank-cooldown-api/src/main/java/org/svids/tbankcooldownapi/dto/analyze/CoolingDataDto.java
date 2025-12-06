package org.svids.tbankcooldownapi.dto.analyze;

public sealed interface CoolingDataDto permits AutoCoolingDataDto, ManualCoolingDataDto {
}
