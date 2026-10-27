package ua.edu.nubip.module2.model;

/**
 * Модель фінансової установи, спільна для лабораторних робіт 8-11.
 * Об'єкт містить ідентифікатор БД, назву, код ліцензії та базовий капітал.
 */
public class FinancialInstitution {
    /** Ідентифікатор запису БД або 0 для об'єкта поза БД. */
    private int id;
    /** Назва фінансової установи. */
    private String name;
    /** Унікальний код ліцензії. */
    private String licenseCode;
    /** Базовий капітал у гривнях. */
    private double baseCapital;

    /**
     * Створює установу без ідентифікатора БД.
     *
     * @param name назва установи
     * @param licenseCode унікальний код ліцензії
     * @param baseCapital базовий капітал у гривнях
     */
    public FinancialInstitution(String name, String licenseCode, double baseCapital) {
        this(0, name, licenseCode, baseCapital);
    }

    /**
     * Створює установу з ідентифікатором БД.
     *
     * @param id ідентифікатор запису або 0 для об'єкта поза БД
     * @param name назва установи
     * @param licenseCode унікальний код ліцензії
     * @param baseCapital базовий капітал у гривнях
     */
    public FinancialInstitution(int id, String name, String licenseCode, double baseCapital) {
        setId(id);
        setName(name);
        setLicenseCode(licenseCode);
        setBaseCapital(baseCapital);
    }

    /**
     * Повертає ідентифікатор запису.
     *
     * @return ID у БД або 0 для звичайного об'єкта
     */
    public int getId() {
        return id;
    }

    /**
     * Установлює ідентифікатор запису.
     *
     * @param id невід'ємний ідентифікатор
     */
    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("ID не може бути від'ємним.");
        }
        this.id = id;
    }

    /**
     * Повертає назву установи.
     *
     * @return непорожня назва
     */
    public String getName() {
        return name;
    }

    /**
     * Змінює назву установи.
     *
     * @param name нова непорожня назва
     */
    public void setName(String name) {
        this.name = validateText(name, "Назва");
    }

    /**
     * Повертає код ліцензії.
     *
     * @return непорожній код ліцензії
     */
    public String getLicenseCode() {
        return licenseCode;
    }

    /**
     * Змінює код ліцензії.
     *
     * @param licenseCode новий непорожній код
     */
    public void setLicenseCode(String licenseCode) {
        this.licenseCode = validateText(licenseCode, "Код ліцензії");
    }

    /**
     * Повертає базовий капітал.
     *
     * @return капітал у гривнях
     */
    public double getBaseCapital() {
        return baseCapital;
    }

    /**
     * Змінює базовий капітал.
     *
     * @param baseCapital невід'ємне скінченне число
     */
    public void setBaseCapital(double baseCapital) {
        if (!Double.isFinite(baseCapital) || baseCapital < 0) {
            throw new IllegalArgumentException("Капітал має бути невід'ємним скінченним числом.");
        }
        this.baseCapital = baseCapital;
    }

    /**
     * Створює незалежну копію поточного об'єкта.
     *
     * @return новий об'єкт із тими самими значеннями
     */
    public FinancialInstitution copy() {
        return new FinancialInstitution(id, name, licenseCode, baseCapital);
    }

    /**
     * Перевіряє обов'язкове текстове поле.
     *
     * @param value значення для перевірки
     * @param fieldName назва поля для повідомлення про помилку
     * @return очищений від крайніх пробілів текст
     */
    private static String validateText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " не може бути порожнім.");
        }
        return value.trim();
    }
}
