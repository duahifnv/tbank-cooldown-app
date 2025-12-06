package org.svids.tbankcooldownapi.dto.parser;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

@Schema(description = "Запрос на парсинг ссылки")
public record ParseLinkRequest(
    @Schema(
        description = "Ссылка на товар в интернет-магазине",
        required = true,
        example = "https://www.wildberries.ru/catalog/27530247/detail.aspx"
    )
    @NotBlank
    @URL
    String url
) {}
