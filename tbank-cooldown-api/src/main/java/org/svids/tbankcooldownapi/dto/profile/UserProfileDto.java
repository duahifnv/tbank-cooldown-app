package org.svids.tbankcooldownapi.dto.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.svids.tbankcooldownapi.entity.PurchaseCategory;

import java.util.Set;

@Schema(description = "Запрос на обновление профиля пользователя")
public record UserProfileDto(
        @Schema(description = "Никнейм пользователя", example = "mfomincev", maxLength = 256)
        @JsonProperty("nickname")
        String nickname,

        @Schema(description = "О себе", example = "I'm Admin!")
        @JsonProperty("about")
        String about,

        @Schema(
                description = "Запрещенные категории покупок",
                example = "[\"ELECTRONICS\", \"CLOTHING\"]",
                type = "array",
                allowableValues = {"ELECTRONICS", "CLOTHING", "FOOD", "HOME", "OTHER"}
        )
        @JsonProperty("bannedCategories")
        Set<PurchaseCategory> bannedCategories,

        @Schema(
                description = "Включен ли автоматический режим охлаждения",
                example = "true"
        )
        @JsonProperty("autoCoolingEnabled")
        boolean autoCoolingEnabled,

        @Schema(description = "Настройки ручного охлаждения")
        @JsonProperty("manualCooling")
        ManualCoolingDto manualCooling,

        @Schema(description = "Настройки автоматического охлаждения")
        @JsonProperty("autoCooling")
        AutoCoolingDto autoCooling
) {
}
