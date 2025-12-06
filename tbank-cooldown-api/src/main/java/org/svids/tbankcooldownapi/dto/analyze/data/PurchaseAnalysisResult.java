package org.svids.tbankcooldownapi.dto.analyze.data;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Результат анализа покупки")
public record PurchaseAnalysisResult(
        @Schema(description = "Требуется ли охлаждение", example = "true")
        boolean isCooling,

        @Schema(description = "Время охлаждения в секундах", example = "3600")
        Integer coolingTimeout,

        @Schema(description = "Включен ли автоматический режим охлаждения", example = "true")
        boolean autoCoolingEnabled,

        @Schema(description = "Является ли категория заблокированной", example = "true")
        boolean bannedCategory,

        @Schema(description = "Данные о настройках охлаждения")
        CoolingData coolingData
) {

    public static PurchaseAnalysisResult withCooling(boolean autoCoolingEnabled,
                                                     CoolingData coolingData,
                                                     Integer coolingTimeout) {
        return new PurchaseAnalysisResult(true, coolingTimeout, autoCoolingEnabled, false, coolingData);
    }

    public static PurchaseAnalysisResult withoutCooling(boolean autoCoolingEnabled,
                                                        CoolingData coolingData) {
        return new PurchaseAnalysisResult(false, 0, autoCoolingEnabled, false, coolingData);
    }

    public static PurchaseAnalysisResult withBannedCategory() {
        return new PurchaseAnalysisResult(false, 0, false, true, null);
    }
}
