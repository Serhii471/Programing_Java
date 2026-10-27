package ua.edu.nubip.module2;

import ua.edu.nubip.module2.db.InstitutionRepository;
import ua.edu.nubip.module2.io.InstitutionFileService;
import ua.edu.nubip.module2.model.FinancialInstitution;
import ua.edu.nubip.module2.model.SampleData;
import ua.edu.nubip.module2.service.InstitutionArrayManager;
import ua.edu.nubip.module2.ui.MainFrame;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Точка входу об'єднаного GUI-проєкту другого модуля.
 */
public final class Main {
    /** Забороняє створення головного службового класу. */
    private Main() {
    }

    /**
     * Запускає графічний інтерфейс або інтеграційну перевірку.
     *
     * @param args передайте {@code --verify} для перевірки без GUI
     */
    public static void main(String[] args) {
        if (args.length > 0 && "--verify".equalsIgnoreCase(args[0])) {
            runVerification();
            return;
        }

        Path projectRoot = resolveProjectRoot();
        Path textFile = projectRoot.resolve("data/institutions.txt");
        Path binaryFile = projectRoot.resolve("data/institutions.dat");
        Path databaseFile = projectRoot.resolve("data/module2.db");

        try {
            FinancialInstitution[] initial = Files.exists(textFile)
                    ? InstitutionFileService.readText(textFile)
                    : SampleData.createInstitutions();
            InstitutionArrayManager manager = new InstitutionArrayManager(initial);
            InstitutionRepository repository = new InstitutionRepository(databaseFile);
            repository.initialize(initial);

            applySystemLookAndFeel();
            SwingUtilities.invokeLater(() -> {
                MainFrame frame = new MainFrame(
                        manager,
                        repository,
                        textFile,
                        binaryFile,
                        projectRoot.resolve("docs/api/index.html")
                );
                frame.setVisible(true);
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Не вдалося запустити проєкт: " + e.getMessage(),
                    "Помилка запуску",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Виконує автоматичну перевірку та повертає код помилки через виняток.
     */
    private static void runVerification() {
        try {
            IntegrationVerifier.run();
        } catch (Exception e) {
            System.err.println("MODULE2_GUI_VERIFICATION_FAILED: " + e.getMessage());
            throw new IllegalStateException(e);
        }
    }

    /**
     * Визначає кореневу папку незалежно від місця запуску команди.
     *
     * @return абсолютний шлях кореня проєкту
     */
    private static Path resolveProjectRoot() {
        String customRoot = System.getProperty("module2.root");
        if (customRoot != null && !customRoot.isBlank()) {
            return Path.of(customRoot).toAbsolutePath().normalize();
        }
        Path current = Path.of("").toAbsolutePath().normalize();
        if (Files.isDirectory(current.resolve("data"))) {
            return current;
        }
        Path nested = current.resolve("Module2_GUI");
        return Files.isDirectory(nested.resolve("data")) ? nested : current;
    }

    /**
     * Вмикає системне оформлення Swing, якщо воно доступне.
     */
    private static void applySystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Стандартне кросплатформне оформлення Swing залишається доступним.
        }
    }
}
