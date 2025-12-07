package org.svids.tbankcooldownapi.service.scrapping;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

@Service
@Slf4j
public class ScrapingService {

    @Value("${scraping.api.key:}")
    private String apiKey;

    @Value("${scraping.api.url:https://api.scraperapi.com}")
    private String apiUrl;

    @Value("${scraping.timeout:60000}")
    private Integer timeoutMs;

    private final List<Map<String, String>> strategies = createScrapingStrategies();

    /**
     * Умный метод с несколькими стратегиями для ScraperAPI
     */
    public Document fetchDocumentSmart(String targetUrl) {
        if (apiKey == null || apiKey.isEmpty()) {
            log.error("ScraperAPI key is not configured");
            return null;
        }

        // Пробуем разные стратегии по порядку
        for (Map<String, String> strategy : strategies) {
            try {
                log.info("Trying ScraperAPI strategy {} for {}", strategy.get("name"), targetUrl);

                Document doc = fetchWithScraperApi(targetUrl, strategy);
                if (doc != null) {
                    log.info("ScraperAPI success with strategy: {}", strategy.get("name"));
                    return doc;
                }
            } catch (Exception e) {
                log.debug("ScraperAPI strategy {} failed: {}", strategy.get("name"), e.getMessage());
            }
        }

        log.error("All ScraperAPI strategies failed for: {}", targetUrl);
        return null;
    }

    private Document fetchWithScraperApi(String targetUrl, Map<String, String> strategy) throws Exception {
        // Параметры ScraperAPI
        Map<String, String> params = new LinkedHashMap<>();
        params.put("api_key", apiKey);
        params.put("url", targetUrl);

        // Добавляем стратегические параметры
        params.putAll(strategy);
        params.remove("name"); // Убираем мета-данные

        // ScraperAPI ожидает параметры по-другому
        String requestUrl = buildScraperApiUrl(params);

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
                .timeout(Duration.ofSeconds(timeoutMs / 1000))
                .header("User-Agent", getRandomUserAgent())
                .header("Accept-Language", "ru-RU,ru;q=0.9")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return Jsoup.parse(response.body());
        } else if (response.statusCode() == 403 || response.statusCode() == 429) {
            // Rate limiting или блокировка
            log.warn("ScraperAPI blocked with status {} for {} with response: {}", response.statusCode(), targetUrl, response.body());
            return null;
        } else {
            log.warn("ScraperAPI strategy {} failed with status: {} with response: {}", strategy.get("name"), response.statusCode(), response.body());
            return null;
        }
    }

    private String buildScraperApiUrl(Map<String, String> params) {
        StringBuilder urlBuilder = new StringBuilder(apiUrl);

        // ScraperAPI имеет другой формат URL
        if (apiUrl.endsWith("/")) {
            urlBuilder.append("?");
        } else {
            urlBuilder.append("/?");
        }

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!"url".equals(entry.getKey()) && !"api_key".equals(entry.getKey())) {
                urlBuilder.append("&")
                        .append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            }
        }

        // api_key и url добавляем в конце
        urlBuilder.append("&api_key=")
                .append(params.get("api_key"))
                .append("&url=")
                .append(URLEncoder.encode(params.get("url"), StandardCharsets.UTF_8));

        return urlBuilder.toString();
    }

    private List<Map<String, String>> createScrapingStrategies() {
        return Arrays.asList(
                // Стратегия 1: Premium Residential прокси для РФ
                Map.of(
                        "name", "premium_residential_ru",
                        "country_code", "ru",
                        "premium", "true",
                        "ultra_premium", "true",
                        "render", "true",
                        "keep_headers", "true",
                        "session_number", "1"
                ),

                // Стратегия 2: Mobile Residential прокси
                Map.of(
                        "name", "mobile_residential",
                        "device_type", "mobile",
                        "country_code", "ru",
                        "premium", "true",
                        "render", "true"
                ),

                // Стратегия 3: Standard Residential без рендеринга
                Map.of(
                        "name", "standard_residential",
                        "country_code", "us", // Иногда зарубежные прокси лучше проходят
                        "render", "false",
                        "premium", "false"
                ),

                // Стратегия 4: Datacenter прокси (дешевле)
                Map.of(
                        "name", "datacenter_proxy",
                        "render", "true",
                        "premium", "false"
                ),

                // Стратегия 5: Специально для JavaScript-heavy сайтов
                Map.of(
                        "name", "js_heavy",
                        "render", "true",
                        "wait_for", "3000",
                        "country_code", "ru",
                        "premium", "true"
                )
        );
    }

    private List<Map<String, String>> getStrategiesForDomain(String domain) {
        // Настраиваем стратегии под конкретные сайты
        if (domain.contains("wildberries")) {
            return Arrays.asList(
                    strategies.get(0), // premium_residential_ru
                    strategies.get(1), // mobile_residential
                    strategies.get(4)  // js_heavy
            );
        } else if (domain.contains("ozon")) {
            return Arrays.asList(
                    strategies.get(0), // premium_residential_ru
                    strategies.get(4), // js_heavy
                    strategies.get(2)  // standard_residential
            );
        } else if (domain.contains("aliexpress") || domain.contains("amazon")) {
            return Arrays.asList(
                    strategies.get(2), // standard_residential (зарубежный)
                    strategies.get(3)  // datacenter_proxy
            );
        }

        // Для остальных сайтов начинаем с премиум
        return strategies;
    }

    /**
     * Альтернативный метод для простых случаев
     */
    public Document fetchSimple(String targetUrl) {
        try {
            // Простой запрос к ScraperAPI
            String apiUrl = String.format(
                    "%s/?api_key=%s&url=%s&render=true&country_code=ru",
                    this.apiUrl, apiKey, URLEncoder.encode(targetUrl, StandardCharsets.UTF_8)
            );

            HttpResponse<String> response;
            try (HttpClient client = HttpClient.newHttpClient()) {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(apiUrl))
                        .timeout(Duration.ofSeconds(30))
                        .GET()
                        .build();

                response = client.send(request, HttpResponse.BodyHandlers.ofString());
            }

            if (response.statusCode() == 200) {
                return Jsoup.parse(response.body());
            } else {
                log.error("ScraperAPI error {}: {}", response.statusCode(), response.body());
                return null;
            }

        } catch (Exception e) {
            log.error("ScraperAPI exception: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Метод для проверки доступности API и баланса
     */
    public Map<String, Object> checkApiStatus() {
        Map<String, Object> status = new HashMap<>();

        try {
            // Тестовый запрос к httpbin для проверки подключения
            String testUrl = "http://httpbin.org/html";
            String apiRequest = String.format(
                    "%s/?api_key=%s&url=%s",
                    apiUrl, apiKey, URLEncoder.encode(testUrl, StandardCharsets.UTF_8)
            );

            HttpResponse<String> response;
            try (HttpClient client = HttpClient.newHttpClient()) {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(apiRequest))
                        .timeout(Duration.ofSeconds(10))
                        .GET()
                        .build();

                response = client.send(request, HttpResponse.BodyHandlers.ofString());
            }

            status.put("connected", response.statusCode() == 200);
            status.put("test_status", response.statusCode());
            status.put("test_url", testUrl);

            // Проверяем ограничения (ограниченная информация из ответа)
            if (response.body().contains("429") || response.body().contains("quota")) {
                status.put("rate_limited", true);
            }

        } catch (Exception e) {
            status.put("connected", false);
            status.put("error", e.getMessage());
        }

        return status;
    }

    private String buildRequestUrl(String apiUrl, Map<String, String> params) {
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

    private String getRandomUserAgent() {
        String[] agents = {
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:121.0) Gecko/20100101 Firefox/121.0",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.2 Safari/605.1.15",
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
        };
        return agents[new Random().nextInt(agents.length)];
    }
}
