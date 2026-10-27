import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Лабораторна робота №11
 * Тема: JDBC-клієнт для простої бази даних
 * Варіант 14: Фінансова установа
 */
class FinancialInstitution {
    private final int id;
    private final String name;
    private final String licenseCode;
    private final double baseCapital;

    public FinancialInstitution(int id, String name, String licenseCode, double baseCapital) {
        this.id = id;
        this.name = validateText(name, "Назва");
        this.licenseCode = validateText(licenseCode, "Код ліцензії");
        if (!Double.isFinite(baseCapital) || baseCapital < 0) {
            throw new IllegalArgumentException("Капітал має бути невід'ємним скінченним числом.");
        }
        this.baseCapital = baseCapital;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLicenseCode() {
        return licenseCode;
    }

    public double getBaseCapital() {
        return baseCapital;
    }

    private static String validateText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " не може бути порожнім.");
        }
        return value.trim();
    }
}

/**
 * Репозиторій ізолює всі SQL-запити та JDBC-операції від меню програми.
 */
class FinancialInstitutionRepository {
    private static final String CREATE_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS financial_institutions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL CHECK (length(trim(name)) > 0),
                license_code TEXT NOT NULL UNIQUE CHECK (length(trim(license_code)) > 0),
                base_capital REAL NOT NULL CHECK (base_capital >= 0)
            )
            """;

    private static final String CREATE_INDEX_SQL = """
            CREATE INDEX IF NOT EXISTS idx_financial_institutions_capital
            ON financial_institutions(base_capital)
            """;

    private static final Object[][] INITIAL_DATA = {
        {"АгроПромінвест Банк", "LIC-140514", 25_000_000.0},
        {"Ощадний Фінансовий Дім", "LIC-140515", 18_500_000.0},
        {"Страхова компанія Гарант", "LIC-140516", 12_000_000.0},
        {"Кредитна спілка Добробут", "LIC-140517", 4_800_000.0},
        {"Інвестиційний фонд Нива", "LIC-140518", 32_400_000.0},
        {"Лізингова компанія Вектор", "LIC-140519", 9_700_000.0},
        {"Український Розвиток Банк", "LIC-140520", 41_000_000.0},
        {"Фінансова компанія Капітал", "LIC-140521", 14_300_000.0},
        {"Пенсійний фонд Майбутнє", "LIC-140522", 21_600_000.0},
        {"Мікрофінанс Партнер", "LIC-140523", 6_250_000.0}
    };

    private final String connectionUrl;

    public FinancialInstitutionRepository(Path databaseFile) {
        Path absolutePath = databaseFile.toAbsolutePath().normalize();
        Path parent = absolutePath.getParent();
        try {
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Class.forName("org.sqlite.JDBC");
        } catch (Exception e) {
            throw new IllegalStateException("Не вдалося підготувати SQLite JDBC: " + e.getMessage(), e);
        }
        this.connectionUrl = "jdbc:sqlite:" + absolutePath;
    }

    public void initializeDatabase() throws SQLException {
        try (Connection connection = openConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(CREATE_TABLE_SQL);
            statement.executeUpdate(CREATE_INDEX_SQL);
        }
        seedInitialDataIfEmpty();
    }

    public List<FinancialInstitution> findAll() throws SQLException {
        String sql = """
                SELECT id, name, license_code, base_capital
                FROM financial_institutions
                ORDER BY id
                """;
        return executeSelection(sql, null);
    }

    public List<FinancialInstitution> findByMinimumCapital(double minimumCapital) throws SQLException {
        validateCapital(minimumCapital);
        String sql = """
                SELECT id, name, license_code, base_capital
                FROM financial_institutions
                WHERE base_capital >= ?
                ORDER BY base_capital DESC, name
                """;
        return executeSelection(sql, statement -> statement.setDouble(1, minimumCapital));
    }

    public int add(String name, String licenseCode, double baseCapital) throws SQLException {
        FinancialInstitution validated = new FinancialInstitution(0, name, licenseCode, baseCapital);
        String sql = """
                INSERT INTO financial_institutions(name, license_code, base_capital)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS
             )) {
            bindInstitution(statement, validated);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("БД не повернула ідентифікатор нового запису.");
    }

    public boolean update(int id, String name, String licenseCode, double baseCapital) throws SQLException {
        validateId(id);
        FinancialInstitution validated = new FinancialInstitution(id, name, licenseCode, baseCapital);
        String sql = """
                UPDATE financial_institutions
                SET name = ?, license_code = ?, base_capital = ?
                WHERE id = ?
                """;

        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindInstitution(statement, validated);
            statement.setInt(4, id);
            return statement.executeUpdate() == 1;
        }
    }

    public boolean delete(int id) throws SQLException {
        validateId(id);
        String sql = "DELETE FROM financial_institutions WHERE id = ?";
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() == 1;
        }
    }

    public int count() throws SQLException {
        try (Connection connection = openConnection();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(
                     "SELECT COUNT(*) FROM financial_institutions"
             )) {
            return result.next() ? result.getInt(1) : 0;
        }
    }

    private Connection openConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(connectionUrl);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA busy_timeout = 5000");
        }
        return connection;
    }

    private void seedInitialDataIfEmpty() throws SQLException {
        if (count() != 0) {
            return;
        }

        String sql = """
                INSERT INTO financial_institutions(name, license_code, base_capital)
                VALUES (?, ?, ?)
                """;
        try (Connection connection = openConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                for (Object[] row : INITIAL_DATA) {
                    statement.setString(1, (String) row[0]);
                    statement.setString(2, (String) row[1]);
                    statement.setDouble(3, (Double) row[2]);
                    statement.addBatch();
                }
                statement.executeBatch();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private List<FinancialInstitution> executeSelection(
            String sql,
            StatementBinder binder
    ) throws SQLException {
        List<FinancialInstitution> institutions = new ArrayList<>();
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (binder != null) {
                binder.bind(statement);
            }
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    institutions.add(new FinancialInstitution(
                            result.getInt("id"),
                            result.getString("name"),
                            result.getString("license_code"),
                            result.getDouble("base_capital")
                    ));
                }
            }
        }
        return institutions;
    }

    private void bindInstitution(
            PreparedStatement statement,
            FinancialInstitution institution
    ) throws SQLException {
        statement.setString(1, institution.getName());
        statement.setString(2, institution.getLicenseCode());
        statement.setDouble(3, institution.getBaseCapital());
    }

    private void validateCapital(double value) {
        if (!Double.isFinite(value) || value < 0) {
            throw new IllegalArgumentException("Капітал має бути невід'ємним скінченним числом.");
        }
    }

    private void validateId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID має бути додатним цілим числом.");
        }
    }

    @FunctionalInterface
    private interface StatementBinder {
        void bind(PreparedStatement statement) throws SQLException;
    }
}

public class Main {
    private static final Path DEFAULT_DATABASE = Path.of("data", "financial_institutions.db");

    public static void main(String[] args) {
        Path databaseFile = resolveDatabasePath();

        try {
            FinancialInstitutionRepository repository =
                    new FinancialInstitutionRepository(databaseFile);
            repository.initializeDatabase();

            System.out.println("Лабораторна робота №11. JDBC-клієнт");
            System.out.println("База даних: " + databaseFile.toAbsolutePath().normalize());

            if (args.length > 0 && "--demo".equalsIgnoreCase(args[0])) {
                runAutomatedDemo(repository);
            } else {
                runInteractiveMenu(repository);
            }
        } catch (SQLException | IllegalStateException e) {
            System.out.println("Помилка БД: " + e.getMessage());
        }
    }

    private static Path resolveDatabasePath() {
        String customPath = System.getProperty("lab11.database");
        if (customPath != null && !customPath.isBlank()) {
            return Path.of(customPath);
        }
        if (Files.isDirectory(Path.of("data")) || !Files.isDirectory(Path.of("..", "data"))) {
            return DEFAULT_DATABASE;
        }
        return Path.of("..", "data", "financial_institutions.db");
    }

    private static void runInteractiveMenu(FinancialInstitutionRepository repository) {
        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                printMenu();
                int command = readInt(scanner, "Оберіть дію: ");

                try {
                    switch (command) {
                        case 1 -> printInstitutions("Усі записи БД:", repository.findAll());
                        case 2 -> addInstitution(scanner, repository);
                        case 3 -> filterInstitutions(scanner, repository);
                        case 4 -> editInstitution(scanner, repository);
                        case 5 -> deleteInstitution(scanner, repository);
                        case 0 -> running = false;
                        default -> System.out.println("Невідома команда. Введіть число від 0 до 5.");
                    }
                } catch (SQLException e) {
                    System.out.println("Операцію не виконано: " + databaseErrorMessage(e));
                } catch (IllegalArgumentException e) {
                    System.out.println("Некоректні дані: " + e.getMessage());
                }
            }
        }
        System.out.println("Роботу з базою даних завершено.");
    }

    private static void addInstitution(
            Scanner scanner,
            FinancialInstitutionRepository repository
    ) throws SQLException {
        String name = readRequiredText(scanner, "Назва установи: ");
        String licenseCode = readRequiredText(scanner, "Код ліцензії: ");
        double capital = readDouble(scanner, "Базовий капітал, грн: ");
        int id = repository.add(name, licenseCode, capital);
        System.out.println("Запис успішно додано. ID = " + id);
    }

    private static void filterInstitutions(
            Scanner scanner,
            FinancialInstitutionRepository repository
    ) throws SQLException {
        double minimum = readDouble(scanner, "Мінімальний базовий капітал, грн: ");
        printInstitutions(
                String.format(Locale.ROOT, "Установи з капіталом від %.2f грн:", minimum),
                repository.findByMinimumCapital(minimum)
        );
    }

    private static void editInstitution(
            Scanner scanner,
            FinancialInstitutionRepository repository
    ) throws SQLException {
        int id = readInt(scanner, "ID запису для редагування: ");
        String name = readRequiredText(scanner, "Нова назва: ");
        String licenseCode = readRequiredText(scanner, "Новий код ліцензії: ");
        double capital = readDouble(scanner, "Новий базовий капітал, грн: ");

        if (repository.update(id, name, licenseCode, capital)) {
            System.out.println("Запис успішно оновлено.");
        } else {
            System.out.println("Запис із ID = " + id + " не знайдено.");
        }
    }

    private static void deleteInstitution(
            Scanner scanner,
            FinancialInstitutionRepository repository
    ) throws SQLException {
        int id = readInt(scanner, "ID запису для видалення: ");
        if (repository.delete(id)) {
            System.out.println("Запис успішно видалено.");
        } else {
            System.out.println("Запис із ID = " + id + " не знайдено.");
        }
    }

    /**
     * Неперервний сценарій для автоматичної перевірки всіх CRUD-операцій.
     * Тимчасовий запис видаляється наприкінці, тому початкові 10 рядків
     * бази залишаються без змін.
     */
    private static void runAutomatedDemo(FinancialInstitutionRepository repository)
            throws SQLException {
        printInstitutions("1. Перегляд початкової БД:", repository.findAll());

        int temporaryId = repository.add(
                "Тестова фінансова установа",
                "LIC-DEMO-UNIQUE",
                17_000_000.0
        );
        System.out.println("\n2. Додано тестовий запис, ID = " + temporaryId);

        printInstitutions(
                "3. Вибірка з капіталом від 20 000 000 грн:",
                repository.findByMinimumCapital(20_000_000.0)
        );

        boolean updated = repository.update(
                temporaryId,
                "Відредагована фінансова установа",
                "LIC-DEMO-UPDATED",
                19_500_000.0
        );
        System.out.println("\n4. Редагування тестового запису: "
                + (updated ? "успішно" : "не виконано"));

        boolean deleted = repository.delete(temporaryId);
        System.out.println("5. Видалення тестового запису: "
                + (deleted ? "успішно" : "не виконано"));
        System.out.println("6. Підсумкова кількість записів: " + repository.count());
    }

    private static void printMenu() {
        System.out.println("""

                ===== МЕНЮ JDBC-КЛІЄНТА =====
                1 - Переглянути всі записи
                2 - Додати новий запис
                3 - Вибірка за мінімальним капіталом
                4 - Редагувати запис
                5 - Видалити запис
                0 - Завершити роботу
                """);
    }

    private static void printInstitutions(
            String title,
            List<FinancialInstitution> institutions
    ) {
        System.out.println("\n" + title);
        System.out.println("----+--------------------------------+--------------+--------------------");
        System.out.printf("%-4s| %-30s | %-12s | %-18s%n",
                "ID", "Назва", "Ліцензія", "Базовий капітал");
        System.out.println("----+--------------------------------+--------------+--------------------");

        if (institutions.isEmpty()) {
            System.out.println("Записів не знайдено.");
            return;
        }
        for (FinancialInstitution institution : institutions) {
            System.out.printf(
                    "%3d | %-30s | %-12s | %15.2f грн%n",
                    institution.getId(),
                    institution.getName(),
                    institution.getLicenseCode(),
                    institution.getBaseCapital()
            );
        }
    }

    private static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.println("Потрібно ввести ціле число.");
            }
        }
    }

    private static double readDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim().replace(',', '.');
            try {
                double result = Double.parseDouble(value);
                if (Double.isFinite(result) && result >= 0) {
                    return result;
                }
            } catch (NumberFormatException ignored) {
                // Повідомлення нижче є спільним для всіх помилкових значень.
            }
            System.out.println("Потрібно ввести невід'ємне число.");
        }
    }

    private static String readRequiredText(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("Поле не може бути порожнім.");
        }
    }

    private static String databaseErrorMessage(SQLException exception) {
        String message = exception.getMessage();
        if (message != null && message.contains("UNIQUE constraint failed")) {
            return "код ліцензії вже використовується іншим записом.";
        }
        if (message != null && message.contains("CHECK constraint failed")) {
            return "порушено обмеження цілісності даних.";
        }
        return message == null ? exception.getClass().getSimpleName() : message;
    }
}
