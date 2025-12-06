package org.svids.tbankcooldownapi.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Slf4j
public class ScrapingService {

    @Value("${scraping.api.key:}")
    private String apiKey;

    @Value("${scraping.api.url:}")
    private String apiUrl;

    public Document fetchRussianSite(String targetUrl) {
        if (apiKey == null || apiKey.isEmpty()) {
            log.error("Scraping API key is not configured");
            return null;
        }

        try {
            // Параметры согласно документации API
            Map<String, String> params = new LinkedHashMap<>();
            params.put("api_key", apiKey);
            params.put("url", targetUrl);
            params.put("wait", "3000");
            params.put("block_resources", "false"); // ВАЖНО: false для российских сайтов
            params.put("premium_proxy", "true");
            params.put("stealth_proxy", "true");

            // Формируем URL
            String apiUrl = buildApiUrl(params);
            log.info("Calling ScrapingApi for: {}", targetUrl);

            // Делаем запрос
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(30))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .timeout(Duration.ofSeconds(60))
                    .header("User-Agent", "ScrapingApi-Client/1.0")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request, HttpResponse.BodyHandlers.ofString());

            log.debug("Response status: {}", response.statusCode());

            if (response.statusCode() == 200) {
                return Jsoup.parse(response.body());
            } else {
                String errorBody = response.body();
                log.error("Scraping error {}: {}", response.statusCode(), errorBody);

                return null;
            }

        } catch (Exception e) {
            log.error("Scraping exception: {}", e.getMessage());
            return null;
        }
    }

    private String buildApiUrl(Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiUrl);

        for (Map.Entry<String, String> entry : params.entrySet()) {
            // Для параметра "url" не кодируем - Scraping ждет обычный URL
            if ("url".equals(entry.getKey())) {
                builder.queryParam(entry.getKey(), entry.getValue());
            } else {
                // Остальные параметры кодируем как обычно
                builder.queryParam(entry.getKey(), entry.getValue());
            }
        }

        return builder.build().toUriString();
    }
}
