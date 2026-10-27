package ua.edu.nubip.module2.db;

import ua.edu.nubip.module2.model.FinancialInstitution;

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

/**
 * JDBC-репозиторій для таблиці фінансових установ із ЛР11.
 */
public class InstitutionRepository {
    /** SQL-команда створення таблиці з ключами й обмеженнями. */
    private static final String CREATE_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS financial_institutions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL CHECK (length(trim(name)) > 0),
                license_code TEXT NOT NULL UNIQUE CHECK (length(trim(license_code)) > 0),
                base_capital REAL NOT NULL CHECK (base_capital >= 0)
            )
            """;

    /** JDBC URL файлової SQLite-бази. */
    private final String connectionUrl;

    /**
     * Створює репозиторій для заданого SQLite-файла.
     *
     * @param databaseFile шлях до файла БД
     */
    public InstitutionRepository(Path databaseFile) {
        try {
            Path absolute = databaseFile.toAbsolutePath().normalize();
            Path parent = absolute.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Class.forName("org.sqlite.JDBC");
            connectionUrl = "jdbc:sqlite:" + absolute;
        } catch (Exception e) {
            throw new IllegalStateException("Не вдалося підготувати SQLite JDBC.", e);
        }
    }

    /**
     * Створює схему та додає початкові записи, якщо таблиця порожня.
     *
     * @param seedData початкові установи
     * @throws SQLException якщо операція БД не вдалася
     */
    public void initialize(FinancialInstitution[] seedData) throws SQLException {
        try (Connection connection = openConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(CREATE_TABLE_SQL);
            statement.executeUpdate("""
                    CREATE INDEX IF NOT EXISTS idx_financial_institutions_capital
                    ON financial_institutions(base_capital)
                    """);
        }
        if (count() == 0) {
            seed(seedData);
        }
    }

    /**
     * Повертає всі записи за ID.
     *
     * @return список установ
     * @throws SQLException якщо запит не виконався
     */
    public List<FinancialInstitution> findAll() throws SQLException {
        return select("""
                SELECT id, name, license_code, base_capital
                FROM financial_institutions
                ORDER BY id
                """, null);
    }

    /**
     * Вибирає установи з капіталом не менше заданого.
     *
     * @param minimumCapital нижня межа капіталу
     * @return відсортований за спаданням список
     * @throws SQLException якщо запит не виконався
     */
    public List<FinancialInstitution> findByMinimumCapital(double minimumCapital)
            throws SQLException {
        validateCapital(minimumCapital);
        return select("""
                SELECT id, name, license_code, base_capital
                FROM financial_institutions
                WHERE base_capital >= ?
                ORDER BY base_capital DESC, name
                """, statement -> statement.setDouble(1, minimumCapital));
    }

    /**
     * Додає установу до БД.
     *
     * @param institution об'єкт без обов'язкового ID
     * @return згенерований ID
     * @throws SQLException якщо вставлення не вдалося
     */
    public int add(FinancialInstitution institution) throws SQLException {
        String sql = """
                INSERT INTO financial_institutions(name, license_code, base_capital)
                VALUES (?, ?, ?)
                """;
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {
            bindInstitution(statement, institution);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("БД не повернула ID нового запису.");
    }

    /**
     * Оновлює запис за ID.
     *
     * @param institution об'єкт із додатним ID та новими значеннями
     * @return {@code true}, якщо запис знайдено
     * @throws SQLException якщо оновлення не вдалося
     */
    public boolean update(FinancialInstitution institution) throws SQLException {
        if (institution.getId() <= 0) {
            throw new IllegalArgumentException("Для редагування потрібен додатний ID.");
        }
        String sql = """
                UPDATE financial_institutions
                SET name = ?, license_code = ?, base_capital = ?
                WHERE id = ?
                """;
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindInstitution(statement, institution);
            statement.setInt(4, institution.getId());
            return statement.executeUpdate() == 1;
        }
    }

    /**
     * Видаляє запис за ID.
     *
     * @param id додатний ідентифікатор
     * @return {@code true}, якщо запис видалено
     * @throws SQLException якщо видалення не вдалося
     */
    public boolean delete(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID має бути додатним.");
        }
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM financial_institutions WHERE id = ?")) {
            statement.setInt(1, id);
            return statement.executeUpdate() == 1;
        }
    }

    /**
     * Повертає кількість записів таблиці.
     *
     * @return невід'ємна кількість
     * @throws SQLException якщо запит не виконався
     */
    public int count() throws SQLException {
        try (Connection connection = openConnection();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(
                     "SELECT COUNT(*) FROM financial_institutions")) {
            return result.next() ? result.getInt(1) : 0;
        }
    }

    /**
     * Відкриває налаштоване JDBC-з'єднання.
     *
     * @return нове з'єднання
     * @throws SQLException якщо БД недоступна
     */
    private Connection openConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(connectionUrl);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA busy_timeout = 5000");
        }
        return connection;
    }

    /**
     * Додає початковий набір однією транзакцією.
     *
     * @param seedData початкові установи
     * @throws SQLException якщо транзакція не виконалась
     */
    private void seed(FinancialInstitution[] seedData) throws SQLException {
        String sql = """
                INSERT INTO financial_institutions(name, license_code, base_capital)
                VALUES (?, ?, ?)
                """;
        try (Connection connection = openConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                for (FinancialInstitution institution : seedData) {
                    bindInstitution(statement, institution);
                    statement.addBatch();
                }
                statement.executeBatch();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    /**
     * Виконує параметризований SELECT.
     *
     * @param sql текст запиту
     * @param binder необов'язковий установник параметрів
     * @return список результатів
     * @throws SQLException якщо запит не виконався
     */
    private List<FinancialInstitution> select(String sql, StatementBinder binder)
            throws SQLException {
        List<FinancialInstitution> result = new ArrayList<>();
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (binder != null) {
                binder.bind(statement);
            }
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    result.add(new FinancialInstitution(
                            rows.getInt("id"),
                            rows.getString("name"),
                            rows.getString("license_code"),
                            rows.getDouble("base_capital")
                    ));
                }
            }
        }
        return result;
    }

    /**
     * Прив'язує три поля установи до PreparedStatement.
     *
     * @param statement підготовлений запит
     * @param institution джерело значень
     * @throws SQLException якщо прив'язка не вдалася
     */
    private void bindInstitution(
            PreparedStatement statement,
            FinancialInstitution institution
    ) throws SQLException {
        statement.setString(1, institution.getName());
        statement.setString(2, institution.getLicenseCode());
        statement.setDouble(3, institution.getBaseCapital());
    }

    /**
     * Перевіряє числовий критерій вибірки.
     *
     * @param value значення капіталу
     */
    private void validateCapital(double value) {
        if (!Double.isFinite(value) || value < 0) {
            throw new IllegalArgumentException("Капітал має бути невід'ємним числом.");
        }
    }

    /** Функція прив'язки параметрів запиту. */
    @FunctionalInterface
    private interface StatementBinder {
        /**
         * Установлює параметри запиту.
         *
         * @param statement підготовлений запит
         * @throws SQLException якщо параметр не встановлено
         */
        void bind(PreparedStatement statement) throws SQLException;
    }
}
