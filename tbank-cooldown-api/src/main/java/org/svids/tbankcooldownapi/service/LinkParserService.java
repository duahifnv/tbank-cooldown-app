package org.svids.tbankcooldownapi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.svids.tbankcooldownapi.dto.parser.ParsedProduct;
import org.svids.tbankcooldownapi.entity.PurchaseCategory;

import java.math.BigDecimal;
import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class LinkParserService {

    private final ScrapingService scrapingService;

    private record ProductInfo(String name, String price) {
    }

    public Optional<ParsedProduct> parseByUrl(String url) {
        try {
            Document doc = scrapingService.fetchDocument(url);
            if (doc == null) {
                return Optional.empty();
            }

            // 1. Пробуем получить из OpenGraph
            String ogTitle = doc.select("meta[property=og:title]").attr("content");
            String ogPrice = doc.select("meta[property=product:price:amount]").attr("content");

            // 2. Пробуем JSON-LD (структурированные данные)
            String jsonLd = extractJsonLd(doc);
            ProductInfo jsonInfo = parseJsonLd(jsonLd);

            // 3. Берем данные из заголовка и цены на странице
            String pageTitle = doc.title();
            String firstPrice = findFirstPrice(doc);

            String productName = !ogTitle.isBlank() ? ogTitle :
                    jsonInfo.name() != null && !jsonInfo.name().isBlank() ? jsonInfo.name() : pageTitle;

            log.debug("First price: {}", firstPrice);

            String priceStr = !ogPrice.isEmpty() ? ogPrice :
                    jsonInfo.price() != null && !jsonInfo.price().isBlank() ? jsonInfo.price() : firstPrice;
            BigDecimal price = parsePrice(priceStr);

            if (productName.isEmpty() || price == null) {
                return Optional.empty();
            }

            PurchaseCategory category = detectCategory(productName);

            return Optional.of(new ParsedProduct(
                    cleanProductName(productName),
                    price,
                    category,
                    extractDomain(url)
            ));

        } catch (Exception e) {
            log.error("Failed to parse URL: {}", url, e);
            return Optional.empty();
        }
    }

    private String extractJsonLd(Document doc) {
        Elements scripts = doc.select("script[type=application/ld+json]");
        for (Element script : scripts) {
            String json = script.html();
            if (json.contains("\"@type\":\"Product\"")) {
                return json;
            }
        }
        return "";
    }

    private ProductInfo parseJsonLd(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);

            String name = root.path("name").asText();
            String price = root.path("offers")
                    .path("price").asText();

            return new ProductInfo(name, price);
        } catch (Exception e) {
            return new ProductInfo(null, null);
        }
    }

    private String findFirstPrice(Document doc) {
        // Ищем элементы с ценой
        List<String> priceSelectors = Arrays.asList(
                ".price", ".product-price", "[itemprop=price]",
                ".js-product-price", ".c-product__price"
        );

        for (String selector : priceSelectors) {
            Elements priceElements = doc.select(selector);
            if (!priceElements.isEmpty() && priceElements.first() != null) {
                return priceElements.first().text();
            }
        }

        // Ищем любые числа с ₽ или руб
        Pattern pricePattern = Pattern.compile("(\\d[\\d\\s]*[.,]?\\d*)\\s*[₽руб]");
        Matcher matcher = pricePattern.matcher(doc.text());
        if (matcher.find()) {
            return matcher.group(1);
        }

        return "";
    }

    private BigDecimal parsePrice(String priceStr) {
        if (priceStr == null || priceStr.isEmpty()) {
            return null;
        }

        // Убираем пробелы, буквы, оставляем цифры и точку/запятую
        String clean = priceStr.replaceAll("[^\\d,.]", "")
                .replace(",", ".")
                .replaceAll("\\s+", "");

        try {
            return new BigDecimal(clean);
        } catch (Exception e) {
            return null;
        }
    }

    private String cleanProductName(String name) {
        return name.replaceAll("\\s*\\|.*", "")  // Убираем всё после |
                .replaceAll("\\s*-.*", "")    // Убираем всё после -
                .replaceAll("\\s+", " ")      // Множественные пробелы в один
                .trim();
    }

    private String extractDomain(String url) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            return host.startsWith("www.") ? host.substring(4) : host;
        } catch (Exception e) {
            return "unknown";
        }
    }

    private PurchaseCategory detectCategory(String productName) {
        String textToAnalyze = productName.toLowerCase();

        // Ключевые слова для каждой категории
        Map<PurchaseCategory, List<String>> categoryKeywords = Map.of(
                PurchaseCategory.ELECTRONICS, List.of(
                        "смартфон", "iphone", "android", "ноутбук", "компьютер",
                        "телевизор", "наушники", "колонки", "гаджет", "планшет",
                        "часы", "фитнес-браслет", "монитор", "клавиатура", "мышь",
                        "видеокарта", "процессор", "оперативная память", "ssd",
                        "powerbank", "зарядное устройство", "кабель"
                ),
                PurchaseCategory.CLOTHING, List.of(
                        "футболка", "рубашка", "блузка", "платье", "юбка",
                        "брюки", "джинсы", "шорты", "куртка", "пальто",
                        "пуховик", "ветровка", "свитер", "кофта", "худи",
                        "обувь", "кроссовки", "туфли", "ботинки", "сапоги",
                        "кеды", "сланцы", "белье", "нижнее белье", "носки",
                        "колготки", "гольфы", "шарф", "перчатки", "шапка",
                        "кепи", "кепка", "бейсболка"
                ),
                PurchaseCategory.FOOD, List.of(
                        "еда", "кофе", "чай", "шоколад",
                        "конфеты", "печенье", "торт", "пирожное", "мороженое",
                        "напиток", "сок", "вода", "газировка", "лимонад",
                        "алкоголь", "вино", "пиво", "водка", "коньяк",
                        "сыр", "колбаса", "мясо", "рыба", "морепродукты",
                        "овощи", "фрукты", "ягоды", "орехи", "сухофрукты",
                        "макароны", "крупа", "рис", "гречка", "овсянка",
                        "молоко", "кефир", "йогурт", "творог", "сметана",
                        "масло", "подсолнечное масло", "оливковое масло"
                ),
                PurchaseCategory.HOME, List.of(
                        "мебель", "стол", "стул", "кресло", "диван", "кровать",
                        "шкаф", "комод", "полка", "стеллаж", "тумба",
                        "посуда", "тарелка", "чашка", "кружка", "стакан",
                        "ложка", "вилка", "нож", "кастрюля", "сковорода",
                        "чайник", "кофейник", "блендер", "миксер", "тостер",
                        "утюг", "пылесос", "стиральная машина", "холодильник",
                        "плита", "духовка", "микроволновка", "кофемашина",
                        "освещение", "лампа", "светильник", "люстра", "торшер",
                        "ковер", "палас", "покрывало", "подушка", "одеяло",
                        "полотенце", "шторы", "гардина", "жалюзи",
                        "инструмент", "дрель", "шуруповерт", "молоток", "отвертка"
                )
        );

        // Считаем совпадения для каждой категории
        Map<PurchaseCategory, Integer> scores = new HashMap<>();

        for (Map.Entry<PurchaseCategory, List<String>> entry : categoryKeywords.entrySet()) {
            PurchaseCategory category = entry.getKey();
            int score = 0;

            for (String keyword : entry.getValue()) {
                if (textToAnalyze.contains(keyword)) {
                    score++;
                }
            }

            scores.put(category, score);
        }

        // Находим категорию с максимальным количеством совпадений
        PurchaseCategory bestCategory = PurchaseCategory.OTHER;
        int maxScore = 0;

        for (Map.Entry<PurchaseCategory, Integer> entry : scores.entrySet()) {
            if (entry.getValue() > maxScore) {
                maxScore = entry.getValue();
                bestCategory = entry.getKey();
            }
        }

        // Если совпадений мало, возвращаем OTHER
        if (maxScore < 2) {
            return PurchaseCategory.OTHER;
        }

        log.debug("Detected category {} for product {} (score: {})",
                bestCategory, productName, maxScore);

        return bestCategory;
    }
}
