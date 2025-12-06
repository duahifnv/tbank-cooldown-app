package org.svids.tbankcooldownapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.svids.tbankcooldownapi.dto.AuthDto;
import org.svids.tbankcooldownapi.dto.parser.ParseLinkRequest;
import org.svids.tbankcooldownapi.dto.parser.ParsedProduct;
import org.svids.tbankcooldownapi.service.LinkParserService;

import java.util.UUID;

@RestController
@RequestMapping("/api/parser")
@Tag(
        name = "Парсер",
        description = "Парсинг товаров из интернет-магазинов"
)
@RequiredArgsConstructor
public class LinkParserController {

    private final LinkParserService parserService;

    @Operation(
        summary = "Парсинг товара по ссылке",
        description = "Извлекает информацию о товаре из ссылки интернет-магазина"
    )
    @PostMapping("/parse")
    public ResponseEntity<ParsedProduct> parseProductLink(@Parameter(
            description = "UUID пользователя из заголовка X-USER-ID",
            required = true,
            example = "35cf7863-8110-46dd-a92d-99175ea9ed38"
    ) @RequestHeader(value = AuthDto.AUTH_HEADER) UUID userId, @RequestBody @Valid ParseLinkRequest request) {
        return ResponseEntity.ok(parserService.parseByUrl(request.url())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Не удалось распознать товар по ссылке")));
    }
}
