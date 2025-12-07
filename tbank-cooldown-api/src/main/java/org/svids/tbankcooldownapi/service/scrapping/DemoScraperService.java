package org.svids.tbankcooldownapi.service.scrapping;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class DemoScraperService {

    private final Map<String, Document> mockData = createMockData();
    private final Random random = new Random();

    @Value("${scraping.demo.enabled:true}")
    private boolean demoEnabled;

    @Value("${scraping.demo.randomize:true}")
    private boolean randomizeData;

    /**
     * Основной метод для получения HTML документа по URL
     */
    public Document fetchDocument(String url) {
        if (!demoEnabled) {
            log.warn("Demo mode is disabled but DemoScraperApiService is being used");
            return null;
        }

        log.info("DEMO MODE: Returning mock data for {}", url);

        // Извлекаем ключ из URL для детерминированного результата
        String key = extractMockKey(url);

        Document document = mockData.getOrDefault(key, createGenericMockPage(url));

        // Добавляем рандомизацию если нужно
        if (randomizeData) {
            document = randomizeDocument(document, url);
        }

        return document;
    }

    /**
     * Метод для проверки доступности сервиса
     */
    public boolean isAvailable() {
        return demoEnabled;
    }

    /**
     * Получение статистики по демо-сервису
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("service", "DemoScraperApiService");
        stats.put("enabled", demoEnabled);
        stats.put("randomize", randomizeData);
        stats.put("mockPagesCount", mockData.size());
        stats.put("mockPages", mockData.keySet());
        stats.put("timestamp", System.currentTimeMillis());
        return stats;
    }

    /**
     * Извлечение ключа из URL для поиска в мок-данных
     */
    private String extractMockKey(String url) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            String path = uri.getPath();

            // Определяем магазин по домену
            if (host.contains("wildberries")) {
                return extractWildberriesKey(path);
            } else if (host.contains("ozon")) {
                return extractOzonKey(path);
            } else if (host.contains("aliexpress")) {
                return "aliexpress-generic";
            } else if (host.contains("citilink")) {
                return "citilink-generic";
            } else if (host.contains("mvideo")) {
                return "mvideo-generic";
            }

            // Общий ключ на основе хэша URL
            return "generic-" + Math.abs(url.hashCode() % 10);

        } catch (URISyntaxException e) {
            log.warn("Invalid URL syntax: {}", url);
            return "generic-error";
        }
    }

    /**
     * Извлечение ключа для Wildberries
     */
    private String extractWildberriesKey(String path) {
        // Пример: /catalog/27530247/detail.aspx
        Pattern pattern = Pattern.compile("/catalog/(\\d+)/");
        Matcher matcher = pattern.matcher(path);

        if (matcher.find()) {
            String article = matcher.group(1);
            // Используем последнюю цифру артикула для выбора мок-данных
            int lastDigit = Integer.parseInt(article.substring(article.length() - 1));

            switch (lastDigit % 3) {
                case 0: return "wb-iphone";
                case 1: return "wb-sneakers";
                case 2: return "wb-laptop";
                default: return "wb-generic";
            }
        }

        return "wb-generic";
    }

    /**
     * Извлечение ключа для Ozon
     */
    private String extractOzonKey(String path) {
        // Пример: /product/noutbuk-asus-vivobook-15-123456789/
        if (path.contains("noutbuk") || path.contains("laptop") || path.contains("ноутбук")) {
            return "ozon-laptop";
        } else if (path.contains("smartfon") || path.contains("iphone") || path.contains("смартфон")) {
            return "ozon-smartphone";
        } else if (path.contains("krossovki") || path.contains("sneakers") || path.contains("кроссовки")) {
            return "ozon-sneakers";
        }

        return "ozon-generic";
    }

    /**
     * Создание универсальной мок-страницы
     */
    private Document createGenericMockPage(String url) {
        try {
            String domain = new URI(url).getHost();
            String productName = generateProductName(domain);
            BigDecimal price = generateRealisticPrice(domain);

            String html = String.format("""
                <html>
                    <head>
                        <title>%s - купить в %s</title>
                        <meta property="og:title" content="%s">
                        <meta property="og:description" content="Демо-товар для тестирования">
                    </head>
                    <body>
                        <div class="product-page">
                            <h1 class="product-title">%s</h1>
                            <div class="price-block">
                                <span class="price">%s</span>
                            </div>
                            <div class="product-description">
                                Это демонстрационный товар для тестирования парсера.
                                В реальном режиме здесь была бы информация с сайта %s.
                            </div>
                            <div class="demo-notice">
                                ⚠️ Демо-режим: данные сгенерированы автоматически
                            </div>
                        </div>
                    </body>
                </html>
                """, productName, domain, productName, productName,
                    formatPrice(price), domain);

            return Jsoup.parse(html);

        } catch (Exception e) {
            // Фолбэк на самый простой вариант
            return Jsoup.parse("""
                <html>
                    <h1>Демо-товар</h1>
                    <div class="price">10 000 ₽</div>
                </html>
                """);
        }
    }

    /**
     * Генерация реалистичного названия товара
     */
    private String generateProductName(String domain) {
        String[] categories = {
                "Смартфон", "Ноутбук", "Наушники", "Умные часы",
                "Кроссовки", "Куртка", "Кофемашина", "Игровая консоль"
        };

        String[] brands = {
                "Apple", "Samsung", "Xiaomi", "ASUS", "Sony",
                "Nike", "Adidas", "The North Face", "De'Longhi"
        };

        String[] models = {
                "Pro", "Air", "Galaxy", "Redmi", "VivoBook",
                "Air Max", "Ultraboost", "Nuptse", "Magnifica"
        };

        String category = categories[random.nextInt(categories.length)];
        String brand = brands[random.nextInt(brands.length)];
        String model = models[random.nextInt(models.length)];

        if (domain.contains("wildberries")) {
            return String.format("%s %s %s 256GB", brand, category, model);
        } else if (domain.contains("ozon")) {
            return String.format("%s %s %s - купить с доставкой", category, brand, model);
        }

        return String.format("%s %s %s", brand, category, model);
    }

    /**
     * Генерация реалистичной цены
     */
    private BigDecimal generateRealisticPrice(String domain) {
        int basePrice;

        if (domain.contains("wildberries") || domain.contains("ozon")) {
            // Российские цены
            basePrice = 10000 + random.nextInt(190000); // 10к - 200к
        } else if (domain.contains("aliexpress")) {
            // Китайские цены (в рублях)
            basePrice = 1000 + random.nextInt(49000); // 1к - 50к
        } else {
            basePrice = 5000 + random.nextInt(95000); // 5к - 100к
        }

        return new BigDecimal(basePrice);
    }

    /**
     * Форматирование цены в российский формат
     */
    private String formatPrice(BigDecimal price) {
        return String.format("%,d ₽", price.intValue()).replace(",", " ");
    }

    /**
     * Рандомизация документа для большей реалистичности
     */
    private Document randomizeDocument(Document doc, String url) {
        try {
            // Рандомизируем цену ±10%
            var priceElements = doc.select(".price, .price-block__final-price, [data-widget='webPrice'] span");
            if (!priceElements.isEmpty()) {
                String currentPrice = priceElements.first().text();
                BigDecimal price = parsePrice(currentPrice);

                if (price != null) {
                    // Изменяем цену на ±10%
                    double factor = 0.9 + random.nextDouble() * 0.2;
                    BigDecimal newPrice = price.multiply(BigDecimal.valueOf(factor))
                            .setScale(0, RoundingMode.HALF_UP);

                    priceElements.first().text(formatPrice(newPrice));
                }
            }

            // Добавляем случайные элементы для реалистичности
            if (random.nextBoolean()) {
                doc.selectFirst("body").append(
                        "<div class='discount-badge'>Скидка " +
                                (10 + random.nextInt(30)) + "%</div>"
                );
            }

            if (random.nextBoolean()) {
                doc.selectFirst("body").append(
                        "<div class='rating'>Рейтинг: " +
                                String.format("%.1f", 3.5 + random.nextDouble() * 1.5) +
                                " ★</div>"
                );
            }

        } catch (Exception e) {
            log.debug("Randomization error: {}", e.getMessage());
        }

        return doc;
    }

    /**
     * Парсинг цены из строки
     */
    private BigDecimal parsePrice(String priceText) {
        try {
            // Убираем всё кроме цифр
            String clean = priceText.replaceAll("[^\\d]", "");
            if (!clean.isEmpty()) {
                return new BigDecimal(clean);
            }
        } catch (Exception e) {
            log.debug("Price parsing error: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Инициализация мок-данных
     */
    private Map<String, Document> createMockData() {
        Map<String, Document> data = new HashMap<>();

        // Wildberries демо
        data.put("wb-iphone", createWildberriesIphonePage());
        data.put("wb-sneakers", createWildberriesSneakersPage());
        data.put("wb-laptop", createWildberriesLaptopPage());
        data.put("wb-generic", createWildberriesGenericPage());

        // Ozon демо
        data.put("ozon-laptop", createOzonLaptopPage());
        data.put("ozon-smartphone", createOzonSmartphonePage());
        data.put("ozon-sneakers", createOzonSneakersPage());
        data.put("ozon-generic", createOzonGenericPage());

        // AliExpress демо
        data.put("aliexpress-generic", createAliExpressPage());

        // Другие магазины
        data.put("citilink-generic", createCitilinkPage());
        data.put("mvideo-generic", createMVideoPage());

        return data;
    }

    /**
     * Создание страницы iPhone для Wildberries
     */
    private Document createWildberriesIphonePage() {
        return Jsoup.parse("""
            <html>
                <head>
                    <title>Смартфон Apple iPhone 15 Pro 256GB - купить в Wildberries</title>
                    <meta property="og:title" content="Смартфон Apple iPhone 15 Pro 256GB">
                    <script type="application/ld+json">
                        {
                            "@context": "https://schema.org",
                            "@type": "Product",
                            "name": "Смартфон Apple iPhone 15 Pro 256GB",
                            "offers": {
                                "@type": "Offer",
                                "price": "129999",
                                "priceCurrency": "RUB"
                            }
                        }
                    </script>
                </head>
                <body>
                    <div class="product-page" data-card-info='{"imtId":27530247,"name":"iPhone 15 Pro"}'>
                        <div class="product-header">
                            <h1 class="product-name">Смартфон Apple iPhone 15 Pro 256GB, «титановый синий»</h1>
                            <div class="product-sku">Артикул: 27530247</div>
                        </div>
                        
                        <div class="price-block">
                            <div class="price-block__wrapper">
                                <div class="price-block__final-price-wrapper">
                                    <span class="price-block__final-price">129 999 ₽</span>
                                </div>
                                <div class="price-block__credit">
                                    <span>10 833 ₽/мес</span>
                                </div>
                            </div>
                        </div>
                        
                        <div class="product-description">
                            <h3>Описание</h3>
                            <p>Новый iPhone 15 Pro с титановым корпусом, чипом A17 Pro и камерой 48 Мп.</p>
                            <ul>
                                <li>Дисплей 6.1" Super Retina XDR</li>
                                <li>Процессор A17 Pro</li>
                                <li>Основная камера 48 Мп</li>
                                <li>Оптический зум 5×</li>
                                <li>Память 256 ГБ</li>
                            </ul>
                        </div>
                        
                        <div class="product-rating">
                            <div class="product-rating__value">4.8</div>
                            <div class="product-rating__count">1 247 отзывов</div>
                        </div>
                        
                        <div class="demo-notice">⚠️ Демо-режим Wildberries</div>
                    </div>
                </body>
            </html>
            """);
    }

    /**
     * Создание страницы кроссовок для Wildberries
     */
    private Document createWildberriesSneakersPage() {
        return Jsoup.parse("""
            <html>
                <head>
                    <title>Кроссовки Nike Air Max 270 - купить в Wildberries</title>
                </head>
                <body>
                    <div data-card-info='{"imtId":12345678,"name":"Nike Air Max 270"}'>
                        <h1>Кроссовки Nike Air Max 270, черные</h1>
                        <div class="price-block">
                            <span class="price-block__final-price">14 999 ₽</span>
                        </div>
                        <div class="demo-notice">⚠️ Демо-режим Wildberries</div>
                    </div>
                </body>
            </html>
            """);
    }

    /**
     * Создание страницы ноутбука для Wildberries
     */
    private Document createWildberriesLaptopPage() {
        return Jsoup.parse("""
            <html>
                <head>
                    <title>Ноутбук ASUS ROG Zephyrus G14 - купить в Wildberries</title>
                </head>
                <body>
                    <div data-card-info='{"imtId":87654321,"name":"ASUS ROG Zephyrus G14"}'>
                        <h1>Ноутбук игровой ASUS ROG Zephyrus G14 GA402</h1>
                        <div class="price-block">
                            <span class="price-block__final-price">189 999 ₽</span>
                        </div>
                        <div class="demo-notice">⚠️ Демо-режим Wildberries</div>
                    </div>
                </body>
            </html>
            """);
    }

    /**
     * Создание общей страницы Wildberries
     */
    private Document createWildberriesGenericPage() {
        return Jsoup.parse("""
            <html>
                <body>
                    <div class="product-page">
                        <h1>Товар Wildberries</h1>
                        <div class="price">25 000 ₽</div>
                        <div class="demo-notice">⚠️ Демо-режим Wildberries</div>
                    </div>
                </body>
            </html>
            """);
    }

    /**
     * Создание страницы ноутбука для Ozon
     */
    private Document createOzonLaptopPage() {
        return Jsoup.parse("""
            <html>
                <div data-widget="webProduct">
                    <h1 data-widget="webProductHeading">Ноутбук ASUS VivoBook 15 X515EA</h1>
                    <div data-widget="webPrice">
                        <div class="price">
                            <span>54 999 ₽</span>
                        </div>
                    </div>
                    <div data-widget="webProductDescription">
                        <p>Ноутбук ASUS VivoBook 15 X515EA-BQ1834</p>
                        <ul>
                            <li>Экран 15.6" Full HD</li>
                            <li>Процессор Intel Core i5</li>
                            <li>Оперативная память 8 ГБ</li>
                            <li>SSD 512 ГБ</li>
                        </ul>
                    </div>
                    <div class="demo-notice">⚠️ Демо-режим Ozon</div>
                </div>
            </html>
            """);
    }

    /**
     * Создание страницы смартфона для Ozon
     */
    private Document createOzonSmartphonePage() {
        return Jsoup.parse("""
            <html>
                <div data-widget="webProduct">
                    <h1>Смартфон Samsung Galaxy S23 Ultra 256GB</h1>
                    <div data-widget="webPrice">
                        <span>89 999 ₽</span>
                    </div>
                    <div class="demo-notice">⚠️ Демо-режим Ozon</div>
                </div>
            </html>
            """);
    }

    /**
     * Создание страницы кроссовок для Ozon
     */
    private Document createOzonSneakersPage() {
        return Jsoup.parse("""
            <html>
                <div data-widget="webProduct">
                    <h1>Кроссовки Adidas Ultraboost Light</h1>
                    <div data-widget="webPrice">
                        <span>12 999 ₽</span>
                    </div>
                    <div class="demo-notice">⚠️ Демо-режим Ozon</div>
                </div>
            </html>
            """);
    }

    /**
     * Создание общей страницы Ozon
     */
    private Document createOzonGenericPage() {
        return Jsoup.parse("""
            <html>
                <div data-widget="webProduct">
                    <h1>Товар Ozon</h1>
                    <div class="price">15 000 ₽</div>
                    <div class="demo-notice">⚠️ Демо-режим Ozon</div>
                </div>
            </html>
            """);
    }

    /**
     * Создание страницы AliExpress
     */
    private Document createAliExpressPage() {
        return Jsoup.parse("""
            <html>
                <div class="product-detail">
                    <h1 class="product-title">Xiaomi Redmi Note 12 Pro</h1>
                    <div class="product-price">
                        <span class="price">23 456 ₽</span>
                    </div>
                    <div class="demo-notice">⚠️ Демо-режим AliExpress</div>
                </div>
            </html>
            """);
    }

    /**
     * Создание страницы Citilink
     */
    private Document createCitilinkPage() {
        return Jsoup.parse("""
            <html>
                <div class="product_data">
                    <h1>Холодильник Samsung RB33</h1>
                    <div class="price">45 990 ₽</div>
                    <div class="demo-notice">⚠️ Демо-режим Citilink</div>
                </div>
            </html>
            """);
    }

    /**
     * Создание страницы М.Видео
     */
    private Document createMVideoPage() {
        return Jsoup.parse("""
            <html>
                <div class="product-card">
                    <h1>Телевизор LG OLED55C3</h1>
                    <div class="price">89 999 ₽</div>
                    <div class="demo-notice">⚠️ Демо-режим М.Видео</div>
                </div>
            </html>
            """);
    }

    /**
     * Метод для переключения режима демо
     */
    public void setDemoEnabled(boolean enabled) {
        this.demoEnabled = enabled;
        log.info("Demo mode {}", enabled ? "enabled" : "disabled");
    }

    /**
     * Метод для переключения рандомизации
     */
    public void setRandomizeData(boolean randomize) {
        this.randomizeData = randomize;
        log.info("Randomization {}", randomize ? "enabled" : "disabled");
    }
}
