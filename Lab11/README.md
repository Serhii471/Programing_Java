# Лабораторна робота №11 - Програмування Java

## Тема: JDBC-клієнт для простої бази даних
## Варіант 14: Фінансова установа

---

## 1. Реалізовані можливості

Консольний JDBC-клієнт працює з однією таблицею `financial_institutions` у файловій БД SQLite та дозволяє:

1. переглядати всі записи;
2. додавати фінансову установу;
3. виконувати вибірку установ із базовим капіталом не менше введеного значення;
4. редагувати запис за його первинним ключем `id`;
5. видаляти запис за `id`.

Для SQL-команд із даними застосовано `PreparedStatement`, що забезпечує коректну передачу параметрів і захист від SQL-ін'єкцій.

---

## 2. Структура БД

Програма автоматично створює файл `data/financial_institutions.db` і таблицю:

```sql
CREATE TABLE IF NOT EXISTS financial_institutions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL CHECK (length(trim(name)) > 0),
    license_code TEXT NOT NULL UNIQUE CHECK (length(trim(license_code)) > 0),
    base_capital REAL NOT NULL CHECK (base_capital >= 0)
);
```

Обмеження цілісності:

- `id` - автоматичний первинний ключ;
- `name` - обов'язкова непорожня назва;
- `license_code` - обов'язковий унікальний код ліцензії;
- `base_capital` - обов'язкове невід'ємне число.

Також створюється індекс для поля `base_capital`. Якщо таблиця порожня, програма додає до неї 10 установ із ЛР10.

Схема продубльована у файлі `data/schema.sql`.

---

## 3. Структура програми

- `FinancialInstitution` - об'єкт предметної області з ЛР10;
- `FinancialInstitutionRepository` - створення схеми та всі CRUD-запити JDBC;
- `Main` - інтерактивне консольне меню;
- `run.ps1` - автоматична компіляція і запуск із JDBC-драйвером.

SQLite є вбудованою файловою БД, тому окремий сервер, логін і пароль не потрібні. Схему, ключі й обмеження реалізовано, але серверний бонус у цій версії не заявляється.

---

## 4. Запуск

У PowerShell відкрийте папку `Lab11` і виконайте:

```powershell
.\run.ps1
```

Якщо політика PowerShell забороняє запуск локального скрипта:

```powershell
powershell -ExecutionPolicy Bypass -File .\run.ps1
```

Ручний запуск:

```powershell
javac -encoding UTF-8 -d out src\Main.java
java -cp "out;lib\sqlite-jdbc-3.53.4.0.jar" Main
```

Автоматичний демонстраційний сценарій усіх CRUD-операцій:

```powershell
java -cp "out;lib\sqlite-jdbc-3.53.4.0.jar" Main --demo
```

Сценарій додає тимчасовий запис, виконує вибірку, редагує і видаляє його. Після завершення в БД залишаються початкові 10 записів.
