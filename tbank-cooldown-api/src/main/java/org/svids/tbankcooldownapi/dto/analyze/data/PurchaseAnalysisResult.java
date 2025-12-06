package org.svids.tbankcooldownapi.dto.analyze.data;

public record PurchaseAnalysisResult(boolean isCooling,
                                     Integer coolingTimeout,
                                     boolean autoCoolingEnabled,
                                     CoolingData coolingData) {

    public static PurchaseAnalysisResult withCooling(boolean autoCoolingEnabled, CoolingData coolingData, Integer coolingTimeout) {
        return new PurchaseAnalysisResult(true, coolingTimeout, autoCoolingEnabled, coolingData);
    }

    public static PurchaseAnalysisResult withoutCooling(boolean autoCoolingEnabled, CoolingData coolingData) {
        return new PurchaseAnalysisResult(false, 0, autoCoolingEnabled, coolingData);
    }
}
