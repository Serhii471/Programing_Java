import java.util.Arrays;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.Scanner;

/*
 * Лабораторна робота №8
 * Тема: Масиви об'єктів
 * Варіант 14: Фінансова установа
 *
 * Теоретичні відповіді:
 * 1. Масив - це структура даних фіксованого розміру, що зберігає
 *    послідовність елементів одного типу. Доступ здійснюється за індексом.
 * 2. Індекс п'ятого за порядком елемента дорівнює 4, оскільки індексація
 *    масивів у Java починається з нуля.
 * 3. Розмір масиву визначають під час створення (new Type[size]) або
 *    кількістю елементів у ініціалізаторі. Поточну довжину отримують через
 *    поле length, наприклад institutions.length.
 * 4. Елементи масиву можуть мати будь-який один тип: примітивний або
 *    посилальний. Для масиву об'єктів це може бути клас, його нащадки або
 *    тип інтерфейсу.
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
 * Клас-каталог агрегує масив фінансових установ і містить усі операції
 * над ним, як рекомендовано в умові лабораторної роботи.
 */
class FinancialInstitutionRegistry {
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
        checkIndex(index);
    }

    public void printAll(String title) {
        System.out.println("\n" + title);
        System.out.println("---+--------------------------------+--------------+--------------------");
        System.out.printf("%-3s| %-30s | %-12s | %-18s%n",
                "№", "Назва", "Ліцензія", "Базовий капітал");
        System.out.println("---+--------------------------------+--------------+--------------------");
        for (int i = 0; i < institutions.length; i++) {
            institutions[i].showInfo(i);
        }
    }

    public void update(int index, String name, String licenseCode, double baseCapital) {
        checkIndex(index);
        // Створюємо новий об'єкт, щоб у разі помилки перевірки старий
        // елемент не залишився частково зміненим.
        institutions[index] = new FinancialInstitution(name, licenseCode, baseCapital);
    }

    public void printWithCapitalAtLeast(double minimumCapital) {
        System.out.printf("%nУстанови з базовим капіталом не менше %.2f грн:%n", minimumCapital);
        System.out.println("---+--------------------------------+--------------+--------------------");
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

    private void checkIndex(int index) {
        if (index < 0 || index >= institutions.length) {
            throw new IndexOutOfBoundsException(
                    "Індекс має бути в межах від 0 до " + (institutions.length - 1) + "."
            );
        }
    }
}

public class Main {
    private static final double FILTER_CAPITAL = 15_000_000.0;

    public static void main(String[] args) {
        FinancialInstitutionRegistry registry = new FinancialInstitutionRegistry(createInitialData());

        System.out.println("Лабораторна робота №8. Масиви об'єктів");
        System.out.println("Варіант 14: Фінансова установа");
        registry.printAll("Початковий масив (індекси починаються з 0):");

        try (Scanner scanner = new Scanner(System.in)) {
            modifySelectedInstitution(scanner, registry);
        } catch (InputMismatchException e) {
            System.out.println("Помилка: для індексу та капіталу потрібно вводити числа.");
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            System.out.println("Помилка: " + e.getMessage());
        }

        registry.printAll("Масив після спроби модифікації:");
        registry.printWithCapitalAtLeast(FILTER_CAPITAL);

        registry.sortByCapitalAscending();
        registry.printAll("Сортування за зростанням базового капіталу:");

        registry.sortByCapitalDescending();
        registry.printAll("Сортування за спаданням базового капіталу:");
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
