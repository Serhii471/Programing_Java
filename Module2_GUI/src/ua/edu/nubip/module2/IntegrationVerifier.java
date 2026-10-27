package ua.edu.nubip.module2;

import ua.edu.nubip.module2.db.InstitutionRepository;
import ua.edu.nubip.module2.io.InstitutionFileService;
import ua.edu.nubip.module2.model.FinancialInstitution;
import ua.edu.nubip.module2.model.SampleData;
import ua.edu.nubip.module2.service.InstitutionArrayManager;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Виконує автоматичну інтеграційну перевірку модулів без відкриття GUI.
 */
public final class IntegrationVerifier {
    /** Забороняє створення службового класу. */
    private IntegrationVerifier() {
    }

    /**
     * Перевіряє масиви, регулярні вирази, файли та JDBC CRUD.
     *
     * @throws Exception якщо хоча б одна перевірка не пройшла
     */
    public static void run() throws Exception {
        InstitutionArrayManager manager =
                new InstitutionArrayManager(SampleData.createInstitutions());

        manager.sortByCapitalAscending();
        require(manager.getAll()[0].getBaseCapital() == 4_800_000.0,
                "Сортування капіталу за зростанням");
        manager.sortByNameDescending();
        require(manager.filterByNameRegex(".*банк$").length == 2,
                "Фільтрація назв за регулярним виразом");
        require(manager.findCapitalExtremes().getMaximum().getBaseCapital() == 41_000_000.0,
                "Пошук максимального капіталу");

        Path temporaryDirectory = Files.createTempDirectory("module2-gui-check-");
        Path binaryFile = temporaryDirectory.resolve("institutions.dat");
        Path databaseFile = temporaryDirectory.resolve("institutions.db");
        try {
            InstitutionFileService.writeBinary(binaryFile, manager.getAll());
            FinancialInstitution[] restored = InstitutionFileService.readBinary(binaryFile);
            require(restored.length == 10, "Двійковий файловий round-trip");

            InstitutionRepository repository = new InstitutionRepository(databaseFile);
            repository.initialize(SampleData.createInstitutions());
            require(repository.count() == 10, "Початкове наповнення БД");

            int id = repository.add(new FinancialInstitution(
                    "Тимчасова установа", "LIC-MODULE2-CHECK", 17_000_000.0));
            require(repository.findByMinimumCapital(20_000_000.0).size() == 4,
                    "Параметризована вибірка JDBC");
            require(repository.update(new FinancialInstitution(
                    id, "Оновлена установа", "LIC-MODULE2-UPDATED", 19_000_000.0)),
                    "Редагування JDBC");
            require(repository.delete(id), "Видалення JDBC");
            require(repository.count() == 10, "Відновлення початкової кількості БД");
        } finally {
            Files.deleteIfExists(binaryFile);
            Files.deleteIfExists(databaseFile);
            Files.deleteIfExists(temporaryDirectory);
        }

        System.out.println("MODULE2_GUI_VERIFICATION_PASSED");
    }

    /**
     * Перериває перевірку, якщо умова хибна.
     *
     * @param condition умова успіху
     * @param label назва перевірки
     */
    private static void require(boolean condition, String label) {
        if (!condition) {
            throw new IllegalStateException("Не пройдено перевірку: " + label);
        }
    }
}
