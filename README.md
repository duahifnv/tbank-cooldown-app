# ImpulseBreaker - Борьба с импульсивными покупками

Данный проект представляет собой **iOS приложение и backend API**, помогающее пользователям бороться с импульсивными покупками через систему "охлаждения" (cooling system).

Проект создан в рамках хакатона за 48 часов.

([https://github.com/user/repo/blob/branch/other_file.md](https://github.com/duahifnv/tbank-cooldown-app/blob/main/demo2.gif))

## Основные функции

- **Анализ покупок на импульсивность**:
    - Автоматический расчет времени "охлаждения" на основе бюджета и зарплаты
    - Ручные настройки охлаждения для разных ценовых диапазонов
    - Определение категорий товаров и запрет определенных категорий

- **Парсинг товаров из маркетплейсов**:
    - Автоматическое извлечение данных о товарах (название, цена, категория)
    - Поддержка Wildberries, Ozon, AliExpress и других магазинов
    - Демо-режим с реалистичными данными для тестирования

- **Управление профилем пользователя**:
    - Настройка автоматического или ручного режима охлаждения
    - Установка запрещенных категорий покупок
    - Отслеживание истории покупок и статистики экономии

## Технологический стек

### Backend (Java/Spring Boot 3)
- **Spring Boot 3** - основной фреймворк
- **Spring Data JPA** - работа с базой данных
- **PostgreSQL** - основная база данных
- **Springdoc OpenAPI** - документация API (Swagger UI)
- **Jsoup** - парсинг HTML страниц
- **ScraperAPI** - обход антибот-защиты маркетплейсов

### Mobile (Swift/SwiftUI)
- **SwiftUI** - современный UI фреймворк
- **URLSession** - сетевое взаимодействие с API
- **Core Data** - локальное кэширование данных

### Инфраструктура
- **Docker** - контейнеризация PostgreSQL
- **Tuna Tunnel** - проброс HTTPS туннелей для демо
- **GitHub** - система контроля версий

## Архитектура API

### Аутентификация
Все запросы (кроме аутентификации) требуют заголовок `X-USER-ID` с UUID пользователя:

```bash
curl -X GET "http://localhost:8080/api/purchases" \
  -H "X-USER-ID: 35cf7863-8110-46dd-a92d-99175ea9ed38"
```

Основные endpoints:
```http request
POST /api/user/authenticate - аутентификация/регистрация пользователя

PUT /api/user/profile - обновление профиля и настроек охлаждения

POST /api/purchases/analysis - анализ покупки на импульсивность

POST /api/parser/parse - парсинг товара по ссылке

GET /api/purchases/wished - список желаемых покупок
```

## Настройка проекта

1. Установите зависимости
Для backend:
```markdown
- Java 17+
- Docker Desktop (для PostgreSQL)
- Maven 3.6+
```

Для iOS:
```markdown
- Xcode 15+
- iOS 17+
- Swift 5.9+
```

2. Настройка базы данных

Создайте файл .env в корне проекта:

### База данных
```dotenv
DB_HOST=localhost
DB_PORT=5432
DB_NAME=impulsebreaker
DB_USER=postgres
DB_PASSWORD=postgres
```

### Парсинг (опционально)
```dotenv
SCRAPERAPI_API_KEY=ваш_ключ_scraperapi
SCRAPINGBEE_API_KEY=ваш_ключ_scrapingbee
```

### Настройки приложения
```dotenv
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=local
```

3. Запуск базы данных
### Запустите PostgreSQL в Docker
```bash
docker compose up postgres -d
```

Или вручную

```bash
docker run --name impulsebreaker-db \
-e POSTGRES_PASSWORD=postgres \
-p 5432:5432 \
-d postgres:15
```

# Деплой
Локальная разработка (spring_profile=local)

## Сборка и запуск
```bash
./mvnw clean package
java -jar target/tbank-cooldown-api.jar \
--spring.profiles.active=local
```

Приложение будет доступно по адресу: http://localhost:8080

Документация API (Swagger UI): http://localhost:8080/swagger-ui.html

Продакшен демо (через Tuna Tunnel)
[!NOTE]
Для демонстрации приложения на хакатоне необходимо развернуть сервис
на защищенном HTTPS хосте. Используйте Tuna Tunnel для проброса туннеля.

Установите Tuna Tunnel:
```powershell
winget install --id yuccastream.tuna
```

## Проверка установки

```bash
tuna --version
```

Настройте токен:

```bash
tuna config save-token <ваш_токен_с_my.tuna.am>
```

Запустите приложение:


## Сборка
```bash
./mvnw clean package -DskipTests
```

## Запуск с prod профилем
```bash
java -jar target/tbank-cooldown-api.jar \
--spring.profiles.active=prod
```

Запустите туннель:

```bash
tuna http 8080
```

Tuna предоставит HTTPS URL вида: https://abc123.tuna.am
