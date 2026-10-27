import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Collator;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Лабораторна робота №10
 * Тема: Робота з текстовими та двійковими файлами
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
 * Клас агрегує масив фінансових установ і містить операції ЛР8-ЛР10.
 */
class FinancialInstitutionRegistry {
    private static final Locale UKRAINIAN_LOCALE = Locale.forLanguageTag("uk-UA");
    private static final int MINIMUM_INSTITUTION_COUNT = 10;
    private static final int MAXIMUM_SAFE_FILE_COUNT = 10_000;

    private final FinancialInstitution[] institutions;

    public FinancialInstitutionRegistry(FinancialInstitution[] institutions) {
        if (institutions == null || institutions.length < MINIMUM_INSTITUTION_COUNT) {
            throw new IllegalArgumentException(
                    "Масив повинен містити щонайменше " + MINIMUM_INSTITUTION_COUNT + " установ."
            );
        }
        this.institutions = Arrays.copyOf(institutions, institutions.length);
    }

    /**
     * Зчитує UTF-8 текстовий файл формату: назва;код_ліцензії;капітал.
     */
    public static FinancialInstitutionRegistry readTextFile(Path file) throws IOException {
        List<FinancialInstitution> loaded = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }

                String[] fields = trimmed.split(";", -1);
                if (fields.length != 3) {
                    throw new IOException(
                            "Рядок " + lineNumber + " повинен містити 3 поля, розділені символом ';'."
                    );
                }

                try {
                    double capital = Double.parseDouble(fields[2].trim().replace(',', '.'));
                    loaded.add(new FinancialInstitution(fields[0], fields[1], capital));
                } catch (NumberFormatException e) {
                    throw new IOException(
                            "Некоректне числове значення капіталу в рядку " + lineNumber + ".",
                            e
                    );
                } catch (IllegalArgumentException e) {
                    throw new IOException("Некоректні дані в рядку " + lineNumber + ": " + e.getMessage(), e);
                }
            }
        }

        if (loaded.size() < MINIMUM_INSTITUTION_COUNT) {
            throw new IOException(
                    "Текстовий файл повинен містити щонайменше "
                            + MINIMUM_INSTITUTION_COUNT + " коректних записів."
            );
        }

        return new FinancialInstitutionRegistry(loaded.toArray(new FinancialInstitution[0]));
    }

    /**
     * Записує типізований двійковий файл: кількість записів, а потім для
     * кожної установи два UTF-рядки та одне число double.
     */
    public void writeBinaryFile(Path file) throws IOException {
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        try (DataOutputStream output = new DataOutputStream(
                new BufferedOutputStream(Files.newOutputStream(file)))) {
            output.writeInt(institutions.length);
            for (FinancialInstitution institution : institutions) {
                output.writeUTF(institution.getName());
                output.writeUTF(institution.getLicenseCode());
                output.writeDouble(institution.getBaseCapital());
            }
        }
    }

    /**
     * Відновлює масив об'єктів із двійкового файла, створеного методом
     * writeBinaryFile().
     */
    public static FinancialInstitutionRegistry readBinaryFile(Path file) throws IOException {
        try (DataInputStream input = new DataInputStream(
                new BufferedInputStream(Files.newInputStream(file)))) {
            int count = input.readInt();
            if (count < MINIMUM_INSTITUTION_COUNT || count > MAXIMUM_SAFE_FILE_COUNT) {
                throw new IOException("Некоректна кількість записів у двійковому файлі: " + count);
            }

            FinancialInstitution[] restored = new FinancialInstitution[count];
            for (int i = 0; i < count; i++) {
                String name = input.readUTF();
                String licenseCode = input.readUTF();
                double baseCapital = input.readDouble();
                restored[i] = new FinancialInstitution(name, licenseCode, baseCapital);
            }
            return new FinancialInstitutionRegistry(restored);
        } catch (EOFException e) {
            throw new IOException("Двійковий файл пошкоджений або записаний не повністю.", e);
        } catch (IllegalArgumentException e) {
            throw new IOException("Двійковий файл містить некоректні дані: " + e.getMessage(), e);
        }
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
        Path textInput = locateTextInput();
        Path binaryOutput = textInput.getParent().resolve("institutions.dat");

        try {
            FinancialInstitutionRegistry registry =
                    FinancialInstitutionRegistry.readTextFile(textInput);

            System.out.println("Лабораторна робота №10");
            System.out.println("Варіант 14: Фінансова установа");
            System.out.println("Текстовий файл прочитано: " + textInput.toAbsolutePath().normalize());
            registry.printAll("Масив, завантажений із текстового файла:");

            tryModifySelectedInstitution(registry);
            demonstrateLab8AndLab9Operations(registry);

            // Для двійкового файла остаточно впорядковуємо масив за назвою.
            registry.sortByNameAscending();
            registry.writeBinaryFile(binaryOutput);
            System.out.println("\nВідсортований масив записано у двійковий файл: "
                    + binaryOutput.toAbsolutePath().normalize());

            FinancialInstitutionRegistry restored =
                    FinancialInstitutionRegistry.readBinaryFile(binaryOutput);
            restored.printAll("Масив, відновлений із двійкового файла:");
            System.out.println("Двійковий файл успішно прочитано, об'єкти відтворено на екрані.");
        } catch (IOException e) {
            System.out.println("Помилка роботи з файлом: " + e.getMessage());
        }
    }

    private static Path locateTextInput() {
        Path fromProjectRoot = Path.of("data", "institutions.txt");
        if (Files.exists(fromProjectRoot)) {
            return fromProjectRoot;
        }
        return Path.of("..", "data", "institutions.txt");
    }

    private static void tryModifySelectedInstitution(FinancialInstitutionRegistry registry) {
        try (Scanner scanner = new Scanner(System.in)) {
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
        } catch (InputMismatchException e) {
            System.out.println("Помилка: для індексу та капіталу потрібно вводити числа.");
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            System.out.println("Помилка: " + e.getMessage());
        }
    }

    private static void demonstrateLab8AndLab9Operations(
            FinancialInstitutionRegistry registry
    ) {
        registry.printAll("Масив після спроби модифікації:");
        registry.printWithCapitalAtLeast(FILTER_CAPITAL);

        registry.sortByCapitalAscending();
        registry.printAll("Сортування за зростанням базового капіталу:");
        registry.sortByCapitalDescending();
        registry.printAll("Сортування за спаданням базового капіталу:");

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
}
