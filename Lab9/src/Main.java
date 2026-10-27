import java.text.Collator;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Лабораторна робота №9
 * Тема: Сортування, пошук за регулярним виразом і пошук екстремумів
 * Варіант 14: Фінансова установа
 */
class FinancialInstitution {
    private String name;
    private String licenseCode;
    private double baseCapital;

    public FinancialInstitution(String name, String licenseCode, double baseCapital) {
        setName(name);
        setLicenseCode(licenseCode);
        setBaseCapital(baseCapital);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Назва установи не може бути порожньою.");
        }
        this.name = name.trim();
    }

    public String getLicenseCode() {
        return licenseCode;
    }

    public void setLicenseCode(String licenseCode) {
        if (licenseCode == null || licenseCode.isBlank()) {
            throw new IllegalArgumentException("Код ліцензії не може бути порожнім.");
        }
        this.licenseCode = licenseCode.trim();
    }

    public double getBaseCapital() {
        return baseCapital;
    }

    public void setBaseCapital(double baseCapital) {
        if (!Double.isFinite(baseCapital) || baseCapital < 0) {
            throw new IllegalArgumentException("Капітал має бути невід'ємним скінченним числом.");
        }
        this.baseCapital = baseCapital;
    }

    public void showInfo(int index) {
        System.out.printf(
                "%2d | %-30s | %-12s | %15.2f грн%n",
                index, name, licenseCode, baseCapital
        );
    }
}

/**
 * Клас агрегує масив фінансових установ і містить усі операції над ним.
 */
class FinancialInstitutionRegistry {
    private static final Locale UKRAINIAN_LOCALE = Locale.forLanguageTag("uk-UA");

    private final FinancialInstitution[] institutions;

    public FinancialInstitutionRegistry(FinancialInstitution[] institutions) {
        if (institutions == null || institutions.length < 10) {
            throw new IllegalArgumentException("Масив повинен містити щонайменше 10 установ.");
        }
        this.institutions = Arrays.copyOf(institutions, institutions.length);
    }

    public int size() {
        return institutions.length;
    }

    public void validateIndex(int index) {
        if (index < 0 || index >= institutions.length) {
            throw new IndexOutOfBoundsException(
                    "Індекс має бути в межах від 0 до " + (institutions.length - 1) + "."
            );
        }
    }

    public void printAll(String title) {
        System.out.println("\n" + title);
        printTableHeader();
        for (int i = 0; i < institutions.length; i++) {
            institutions[i].showInfo(i);
        }
    }

    public void update(int index, String name, String licenseCode, double baseCapital) {
        validateIndex(index);
        institutions[index] = new FinancialInstitution(name, licenseCode, baseCapital);
    }

    public void printWithCapitalAtLeast(double minimumCapital) {
        System.out.printf("%nУстанови з базовим капіталом не менше %.2f грн:%n", minimumCapital);
        printTableHeader();

        boolean found = false;
        for (int i = 0; i < institutions.length; i++) {
            if (institutions[i].getBaseCapital() >= minimumCapital) {
                institutions[i].showInfo(i);
                found = true;
            }
        }
        if (!found) {
            System.out.println("Установ, що задовольняють умову, не знайдено.");
        }
    }

    public void sortByCapitalAscending() {
        Arrays.sort(institutions, Comparator.comparingDouble(FinancialInstitution::getBaseCapital));
    }

    public void sortByCapitalDescending() {
        Arrays.sort(
                institutions,
                Comparator.comparingDouble(FinancialInstitution::getBaseCapital).reversed()
        );
    }

    public void sortByNameAscending() {
        Arrays.sort(institutions, createNameComparator());
    }

    public void sortByNameDescending() {
        Arrays.sort(institutions, createNameComparator().reversed());
    }

    /**
     * Виводить установи, назви яких повністю відповідають регулярному виразу.
     */
    public void printMatchingName(String regularExpression) {
        Pattern pattern = Pattern.compile(
                regularExpression,
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
        );

        System.out.printf("%nУстанови, назви яких відповідають виразу \"%s\":%n", regularExpression);
        printTableHeader();

        boolean found = false;
        for (int i = 0; i < institutions.length; i++) {
            if (pattern.matcher(institutions[i].getName()).matches()) {
                institutions[i].showInfo(i);
                found = true;
            }
        }
        if (!found) {
            System.out.println("Установ із відповідною назвою не знайдено.");
        }
    }

    public void printCapitalExtremes() {
        FinancialInstitution minimum = institutions[0];
        FinancialInstitution maximum = institutions[0];

        for (int i = 1; i < institutions.length; i++) {
            FinancialInstitution current = institutions[i];
            if (current.getBaseCapital() < minimum.getBaseCapital()) {
                minimum = current;
            }
            if (current.getBaseCapital() > maximum.getBaseCapital()) {
                maximum = current;
            }
        }

        NumberFormat numberFormat = NumberFormat.getNumberInstance(UKRAINIAN_LOCALE);
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);

        System.out.printf(
                "%nНайменший базовий капітал %s грн має установа \"%s\", "
                        + "найбільший - %s грн - має установа \"%s\".%n",
                numberFormat.format(minimum.getBaseCapital()),
                minimum.getName(),
                numberFormat.format(maximum.getBaseCapital()),
                maximum.getName()
        );
    }

    private Comparator<FinancialInstitution> createNameComparator() {
        Collator collator = Collator.getInstance(UKRAINIAN_LOCALE);
        collator.setStrength(Collator.PRIMARY);
        return (left, right) -> collator.compare(left.getName(), right.getName());
    }

    private void printTableHeader() {
        System.out.println("---+--------------------------------+--------------+--------------------");
        System.out.printf("%-3s| %-30s | %-12s | %-18s%n",
                "№", "Назва", "Ліцензія", "Базовий капітал");
        System.out.println("---+--------------------------------+--------------+--------------------");
    }
}

public class Main {
    private static final double FILTER_CAPITAL = 15_000_000.0;
    private static final String BANK_NAME_REGEX = ".*банк$";

    public static void main(String[] args) {
        FinancialInstitutionRegistry registry = new FinancialInstitutionRegistry(createInitialData());

        System.out.println("Лабораторна робота №9");
        System.out.println("Варіант 14: Фінансова установа");
        registry.printAll("Початковий масив (індекси починаються з 0):");

        try (Scanner scanner = new Scanner(System.in)) {
            modifySelectedInstitution(scanner, registry);
        } catch (InputMismatchException e) {
            System.out.println("Помилка: для індексу та капіталу потрібно вводити числа.");
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            System.out.println("Помилка: " + e.getMessage());
        }

        // Операції, реалізовані в ЛР8.
        registry.printAll("Масив після спроби модифікації:");
        registry.printWithCapitalAtLeast(FILTER_CAPITAL);
        registry.sortByCapitalAscending();
        registry.printAll("Сортування за зростанням базового капіталу:");
        registry.sortByCapitalDescending();
        registry.printAll("Сортування за спаданням базового капіталу:");

        // Нові операції ЛР9.
        registry.sortByNameAscending();
        registry.printAll("Сортування за зростанням текстового поля name:");
        registry.sortByNameDescending();
        registry.printAll("Сортування за спаданням текстового поля name:");

        try {
            registry.printMatchingName(BANK_NAME_REGEX);
        } catch (PatternSyntaxException e) {
            System.out.println("Помилка в регулярному виразі: " + e.getDescription());
        }

        registry.printCapitalExtremes();
    }

    private static void modifySelectedInstitution(
            Scanner scanner,
            FinancialInstitutionRegistry registry
    ) {
        System.out.printf("%nОберіть індекс установи для зміни (0-%d): ", registry.size() - 1);
        int index = scanner.nextInt();
        scanner.nextLine();
        registry.validateIndex(index);

        System.out.print("Введіть нову назву: ");
        String name = scanner.nextLine();

        System.out.print("Введіть новий код ліцензії: ");
        String licenseCode = scanner.nextLine();

        System.out.print("Введіть новий базовий капітал, грн: ");
        double baseCapital = scanner.nextDouble();

        registry.update(index, name, licenseCode, baseCapital);
        System.out.println("Елемент масиву успішно змінено.");
    }

    private static FinancialInstitution[] createInitialData() {
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
