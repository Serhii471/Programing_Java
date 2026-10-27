package ua.edu.nubip.module2.service;

import ua.edu.nubip.module2.model.FinancialInstitution;

import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Агрегує масив фінансових установ і реалізує операції ЛР8 та ЛР9.
 */
public class InstitutionArrayManager {
    /** Українська локаль для алфавітного сортування. */
    private static final Locale UKRAINIAN_LOCALE = Locale.forLanguageTag("uk-UA");

    /** Агрегований масив об'єктів предметної області. */
    private FinancialInstitution[] institutions;

    /**
     * Створює менеджер із початковим масивом.
     *
     * @param institutions масив щонайменше з десяти об'єктів
     */
    public InstitutionArrayManager(FinancialInstitution[] institutions) {
        replaceAll(institutions);
    }

    /**
     * Повертає глибоку копію всіх об'єктів.
     *
     * @return незалежний масив установ
     */
    public FinancialInstitution[] getAll() {
        return Arrays.stream(institutions)
                .map(FinancialInstitution::copy)
                .toArray(FinancialInstitution[]::new);
    }

    /**
     * Замінює вміст агрегованого масиву.
     *
     * @param newInstitutions новий масив щонайменше з десяти об'єктів
     */
    public final void replaceAll(FinancialInstitution[] newInstitutions) {
        if (newInstitutions == null || newInstitutions.length < 10) {
            throw new IllegalArgumentException("Масив має містити щонайменше 10 установ.");
        }
        institutions = Arrays.stream(newInstitutions)
                .map(FinancialInstitution::copy)
                .toArray(FinancialInstitution[]::new);
    }

    /**
     * Змінює об'єкт, знайдений за кодом ліцензії.
     *
     * @param currentLicense поточний код ліцензії
     * @param replacement нові значення об'єкта
     */
    public void updateByLicense(String currentLicense, FinancialInstitution replacement) {
        for (int i = 0; i < institutions.length; i++) {
            if (institutions[i].getLicenseCode().equals(currentLicense)) {
                institutions[i] = replacement.copy();
                return;
            }
        }
        throw new IllegalArgumentException("Установу з ліцензією " + currentLicense + " не знайдено.");
    }

    /**
     * Відбирає установи з капіталом не менше заданого.
     *
     * @param minimumCapital нижня межа капіталу
     * @return новий масив відібраних об'єктів
     */
    public FinancialInstitution[] filterByMinimumCapital(double minimumCapital) {
        if (!Double.isFinite(minimumCapital) || minimumCapital < 0) {
            throw new IllegalArgumentException("Мінімальний капітал має бути невід'ємним числом.");
        }
        return Arrays.stream(institutions)
                .filter(item -> item.getBaseCapital() >= minimumCapital)
                .map(FinancialInstitution::copy)
                .toArray(FinancialInstitution[]::new);
    }

    /** Сортує масив за зростанням базового капіталу. */
    public void sortByCapitalAscending() {
        Arrays.sort(institutions, Comparator.comparingDouble(FinancialInstitution::getBaseCapital));
    }

    /** Сортує масив за спаданням базового капіталу. */
    public void sortByCapitalDescending() {
        Arrays.sort(institutions,
                Comparator.comparingDouble(FinancialInstitution::getBaseCapital).reversed());
    }

    /** Сортує масив за зростанням української назви. */
    public void sortByNameAscending() {
        Arrays.sort(institutions, createNameComparator());
    }

    /** Сортує масив за спаданням української назви. */
    public void sortByNameDescending() {
        Arrays.sort(institutions, createNameComparator().reversed());
    }

    /**
     * Відбирає назви за регулярним виразом без урахування регістру.
     *
     * @param regularExpression регулярний вираз Java
     * @return новий масив об'єктів, назви яких відповідають виразу
     */
    public FinancialInstitution[] filterByNameRegex(String regularExpression) {
        Pattern pattern = Pattern.compile(
                regularExpression,
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
        );
        return Arrays.stream(institutions)
                .filter(item -> pattern.matcher(item.getName()).matches())
                .map(FinancialInstitution::copy)
                .toArray(FinancialInstitution[]::new);
    }

    /**
     * Знаходить установи з мінімальним і максимальним капіталом.
     *
     * @return об'єкт із двома екстремальними значеннями
     */
    public CapitalExtremes findCapitalExtremes() {
        FinancialInstitution minimum = institutions[0];
        FinancialInstitution maximum = institutions[0];
        for (int i = 1; i < institutions.length; i++) {
            if (institutions[i].getBaseCapital() < minimum.getBaseCapital()) {
                minimum = institutions[i];
            }
            if (institutions[i].getBaseCapital() > maximum.getBaseCapital()) {
                maximum = institutions[i];
            }
        }
        return new CapitalExtremes(minimum.copy(), maximum.copy());
    }

    /**
     * Створює компаратор українських назв.
     *
     * @return локалізований компаратор установ
     */
    private Comparator<FinancialInstitution> createNameComparator() {
        Collator collator = Collator.getInstance(UKRAINIAN_LOCALE);
        collator.setStrength(Collator.PRIMARY);
        return (left, right) -> collator.compare(left.getName(), right.getName());
    }

    /**
     * Результат пошуку мінімального та максимального капіталу.
     */
    public static final class CapitalExtremes {
        /** Установа з мінімальним капіталом. */
        private final FinancialInstitution minimum;
        /** Установа з максимальним капіталом. */
        private final FinancialInstitution maximum;

        /**
         * Створює результат пошуку екстремумів.
         *
         * @param minimum установа з мінімальним капіталом
         * @param maximum установа з максимальним капіталом
         */
        private CapitalExtremes(
                FinancialInstitution minimum,
                FinancialInstitution maximum
        ) {
            this.minimum = minimum;
            this.maximum = maximum;
        }

        /**
         * Повертає установу з мінімальним капіталом.
         *
         * @return копія установи
         */
        public FinancialInstitution getMinimum() {
            return minimum.copy();
        }

        /**
         * Повертає установу з максимальним капіталом.
         *
         * @return копія установи
         */
        public FinancialInstitution getMaximum() {
            return maximum.copy();
        }
    }
}
