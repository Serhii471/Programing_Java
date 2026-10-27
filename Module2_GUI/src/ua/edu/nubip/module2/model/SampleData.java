package ua.edu.nubip.module2.model;

/**
 * Фабрика початкових даних варіанта 14.
 */
public final class SampleData {
    /** Забороняє створення службового класу. */
    private SampleData() {
    }

    /**
     * Створює десять фінансових установ, використаних у ЛР8-ЛР11.
     *
     * @return новий масив початкових об'єктів
     */
    public static FinancialInstitution[] createInstitutions() {
        return new FinancialInstitution[] {
            new FinancialInstitution("АгроПромінвест Банк", "LIC-140514", 25_000_000.0),
            new FinancialInstitution("Ощадний Фінансовий Дім", "LIC-140515", 18_500_000.0),
            new FinancialInstitution("Страхова компанія Гарант", "LIC-140516", 12_000_000.0),
            new FinancialInstitution("Кредитна спілка Добробут", "LIC-140517", 4_800_000.0),
            new FinancialInstitution("Інвестиційний фонд Нива", "LIC-140518", 32_400_000.0),
            new FinancialInstitution("Лізингова компанія Вектор", "LIC-140519", 9_700_000.0),
            new FinancialInstitution("Український Розвиток Банк", "LIC-140520", 41_000_000.0),
            new FinancialInstitution("Фінансова компанія Капітал", "LIC-140521", 14_300_000.0),
            new FinancialInstitution("Пенсійний фонд Майбутнє", "LIC-140522", 21_600_000.0),
            new FinancialInstitution("Мікрофінанс Партнер", "LIC-140523", 6_250_000.0)
        };
    }
}
