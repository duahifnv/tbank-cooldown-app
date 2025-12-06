package org.svids.tbankcooldownapi.dto.purchase;

import org.svids.tbankcooldownapi.dto.analyze.CoolingDataDto;

public record PurchaseAnalysisResult(boolean isCooling,
                                     Integer coolingTimeout,
                                     boolean autoCoolingEnabled,
                                     CoolingDataDto coolingData) {

    public static PurchaseAnalysisResult withCooling(boolean autoCoolingEnabled, CoolingDataDto coolingData, Integer coolingTimeout) {
        return new PurchaseAnalysisResult(true, coolingTimeout, autoCoolingEnabled, coolingData);
    }

    public static PurchaseAnalysisResult withoutCooling(boolean autoCoolingEnabled, CoolingDataDto coolingData) {
        return new PurchaseAnalysisResult(false, 0, autoCoolingEnabled, coolingData);
    }
}
