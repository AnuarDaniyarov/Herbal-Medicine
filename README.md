# Medicine Platform — исследовательская платформа

Платформа помогает находить и сопоставлять растения, образцы, экстракты, соединения и опубликованные экспериментальные сведения. Это исследовательская система поддержки принятия решений. Она не предназначена для диагностики, назначения лечения или подтверждения эффективности и безопасности лекарств.

## Состояние модели

Prediction API поддерживает локальный AChE ML-baseline для одного activity code: человеческий IC50. Модель обучается скриптом на подготовленных IMPPAT записях и возвращает `status=ML_BASELINE`, вероятность и метрики. Она небольшая, exploratory, обучена на 74 записях с одним случайным split, без scaffold split; данные и структура не прошли полную проверку. Остальные типы активности явно помечаются как `DEMO`. Методика и ограничения — в [docs/ml-baseline.md](docs/ml-baseline.md).

Если доступны локальные подготовленные данные, модель можно переобучить:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\scripts\Train-AcheBaseline.ps1
```

Артефакт остаётся в игнорируемом `data/processed/ache-baseline-model.json` и не попадает в Git. Для Compose каталог данных монтируется только для чтения в backend container.

## Требования

- Java 21;
- PostgreSQL 15+;
- Docker Compose для запуска полного backend-стека либо Maven Wrapper для локального запуска приложения.

## Запуск через Docker Compose

Скопируйте пример переменных окружения и измените локальный пароль базы данных:

```powershell
Copy-Item .env.example .env
```

Запустите PostgreSQL и backend:

```powershell
docker compose up --build
```

Backend будет доступен на `http://localhost:8081`, PostgreSQL — только на loopback-интерфейсе `127.0.0.1:5432`. Flyway применит миграции автоматически. Для остановки выполните `docker compose down`; данные PostgreSQL остаются в именованном volume. Удаление этого volume удалит локальную базу.

## Локальный запуск backend

Создайте PostgreSQL-базу `medicine_platform`, затем задайте параметры подключения в PowerShell:

```powershell
$env:DB_URL = 'jdbc:postgresql://localhost:5432/medicine_platform'
$env:DB_USERNAME = 'postgres'
$env:DB_PASSWORD = 'your-local-password'
$env:CORS_ALLOWED_ORIGINS = 'http://localhost:4200'
.\mvnw.cmd spring-boot:run
```

Либо внесите параметры в локальный `.env` (он исключён из Git) и запустите собранный backend через `.\scripts\start-backend.ps1`. Spring Boot сам не читает `.env`; скрипт загружает значения в окружение процесса и завершает запуск с понятной ошибкой, если пароль пустой или осталась заглушка.

Пароль не хранится в `application.yaml`; передавайте его через окружение. Переменная `CORS_ALLOWED_ORIGINS` принимает список origin через запятую. По умолчанию разрешён только локальный Angular dev server `http://localhost:4200`.

## API

- Swagger UI: `http://localhost:8081/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8081/v3/api-docs`
- Health check: `http://localhost:8081/actuator/health`

Каталожные списки используют `page`, `size` (максимум 100) и Spring Data `sort`.

| Метод | Путь | Назначение |
| --- | --- | --- |
| GET | `/api/plants?query=...&family=...` | Поиск, фильтр по семейству и пагинация растений |
| GET | `/api/plants/{id}` | Карточка растения |
| GET | `/api/plants/{id}/samples` | Образцы растения |
| GET | `/api/plants/{id}/compounds` | Связанные соединения |
| GET | `/api/samples/{id}` | Карточка образца |
| GET | `/api/samples/{id}/extracts` | Экстракты образца |
| GET | `/api/extracts/{id}` | Карточка экстракта |
| GET | `/api/extracts/{id}/measurements` | Измерения активности экстракта |
| GET | `/api/compounds` | Поиск и фильтрация соединений |
| GET | `/api/compounds/{id}` | Карточка соединения и происхождение |
| GET | `/api/compounds/{id}/measurements` | Измерения соединения |
| GET | `/api/assays` | Тесты и типы активности |
| GET | `/api/targets` | Нормализованные биологические мишени |
| GET | `/api/sources/{id}` | Источник и примечание о лицензии |
| POST | `/api/predictions` | Сохранить AChE ML-baseline или явно помеченный demo-запрос (`201`) |
| GET | `/api/predictions/{id}` | Получить сохранённый результат и версию провайдера |
| POST | `/api/comparisons` | Сопоставить 2–20 соединений по одному activity code |
| GET | `/api/reports/candidates` | Скачать CSV кандидатов и измерений |

Пример сравнения:

```json
{
  "candidateIds": [12, 34],
  "activityCode": "imppat_ic50_example"
}
```

Сравнение возвращает каждое экспериментальное измерение отдельно вместе с assay, единицей, отношением (`<`, `=`, `>`) и источником. Значения из разных assay или единиц не сводятся автоматически к общему баллу. CSV принимает необязательные `activityCode` и повторяемые `candidateIds`; без ID выгружается весь каталог соединений с измерениями, если они есть.

Ошибки API возвращают JSON-поля `timestamp`, `status`, `errorCode`, `message`, `path` и `fieldErrors`. SMILES проверяется на непустое значение и длину, но химическая валидация без химической библиотеки не выполняется.

## Схема данных и происхождение

Flyway миграции находятся в `src/main/resources/db/migration`. Hibernate настроен на `ddl-auto: validate` и не меняет схему автоматически. Миграция V3 добавляет образцы растений, экстракты, мишени, ссылки связей на образцы/экстракты и измерения экстрактов. Миграция V4 расширяет точность численного результата до `NUMERIC(38,12)`, чтобы вместить крупные опубликованные значения без обрезания. Измерение относится ровно к одному соединению или экстракту; исходные assay, единица, условия, качество и источник сохраняются отдельно.

Подготовка и загрузка IMPPAT 3.0 описана в [docs/data-import.md](docs/data-import.md), предварительная проверка применимости данных — в [docs/data-audit.md](docs/data-audit.md). Исходные таблицы и архивы не загружаются автоматически. Проверяйте условия лицензии и цитирования до распространения подготовленных данных. Сведения из подготовленного набора помечаются как непроверенные; наличие записи не является самостоятельным подтверждением результата.

## Тесты

```powershell
.\mvnw.cmd test
```

Интеграционные тесты запускают все Flyway-миграции на H2 и проверяют каталоги, источники, измерения, demo- и ML-baseline провайдеры, прогнозирование, валидацию, OpenAPI, сравнение, CSV и CORS. Для проверки PostgreSQL используйте Compose и приложение; отдельный Testcontainers профиль пока не добавлен.

## Запуск Angular-интерфейса

Требуются Node.js 24.15+ и npm 11. В отдельном PowerShell-окне из каталога `frontend` запустите:

```powershell
# В управляемых shell можно убрать ограничивающую переменную npm:
Remove-Item Env:npm_config_allow_scripts -ErrorAction SilentlyContinue
npm ci
npm start
```

Интерфейс откроется на `http://localhost:4200`. Dev proxy направляет `/api` в backend на `http://localhost:8081`. Для обычного локального запуска backend должен быть запущен отдельно; база подключается через `DB_URL`, `DB_USERNAME` и `DB_PASSWORD`.

Полный локальный стек запускается через `docker compose up --build`: Angular доступен на `http://localhost:4200`, API — `http://localhost:8081`, PostgreSQL — `localhost:5432`. Nginx в контейнере интерфейса перенаправляет `/api` в backend. Данные БД сохраняются в именованном volume.

Frontend содержит главную страницу, каталоги растений и соединений с поиском и пагинацией, карточки записей, источники/измерения, AChE baseline-оценку, demo-режим для неподдерживаемых активностей, сравнение кандидатов и экспорт CSV. Выбранные кандидаты сохраняются в localStorage браузера. Ограничения baseline и demo-результатов явно показаны в интерфейсе.

Проверка frontend:

```powershell
Remove-Item Env:npm_config_allow_scripts -ErrorAction SilentlyContinue
npm run build
npm test -- --watch=false
```

Сведения каталога IMPPAT 3.0 были импортированы локально из пользовательской папки. Перед распространением данных перепроверьте условия и цитирование на [официальной странице IMPPAT](https://cb.imsc.res.in/imppat/) и [странице загрузки](https://cb.imsc.res.in/imppat/download). Публичный репозиторий не должен автоматически включать исходные или обработанные файлы каталога.



## User guide

Full step-by-step usage instructions in [Russian](docs/ИНСТРУКЦИЯ-ПОЛЬЗОВАТЕЛЯ.md).
