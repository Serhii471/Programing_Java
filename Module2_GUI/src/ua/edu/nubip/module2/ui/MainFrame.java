package ua.edu.nubip.module2.ui;

import ua.edu.nubip.module2.db.InstitutionRepository;
import ua.edu.nubip.module2.io.InstitutionFileService;
import ua.edu.nubip.module2.model.FinancialInstitution;
import ua.edu.nubip.module2.service.InstitutionArrayManager;
import ua.edu.nubip.module2.service.InstitutionArrayManager.CapitalExtremes;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.PatternSyntaxException;

/**
 * Головне вікно, що об'єднує лабораторні роботи другого модуля.
 */
public class MainFrame extends JFrame {
    /** Версія серіалізації Swing-вікна. */
    private static final long serialVersionUID = 1L;
    /** Основний темно-синій колір інтерфейсу. */
    private static final Color PRIMARY = new Color(28, 67, 105);
    /** Акцентний зелений колір дій. */
    private static final Color ACCENT = new Color(32, 122, 93);
    /** Українська локаль для форматування чисел. */
    private static final Locale UKRAINIAN_LOCALE = Locale.forLanguageTag("uk-UA");

    /** Спільний менеджер масиву ЛР8-ЛР10. */
    private final InstitutionArrayManager manager;
    /** JDBC-репозиторій вкладки ЛР11. */
    private final InstitutionRepository repository;
    /** Шлях до текстового файла ЛР10. */
    private final Path textFile;
    /** Шлях до двійкового файла ЛР10. */
    private final Path binaryFile;
    /** Шлях до головної HTML-сторінки Javadoc. */
    private final Path apiIndex;

    /** Модель таблиці вкладки ЛР8. */
    private final DefaultTableModel arrayModel = createReadOnlyModel(false);
    /** Модель таблиці вкладки ЛР9. */
    private final DefaultTableModel textModel = createReadOnlyModel(false);
    /** Модель таблиці вкладки ЛР10. */
    private final DefaultTableModel fileModel = createReadOnlyModel(false);
    /** Модель таблиці вкладки ЛР11. */
    private final DefaultTableModel databaseModel = createReadOnlyModel(true);
    /** Журнал файлових операцій. */
    private final JTextArea fileLog = new JTextArea();
    /** Нижній статусний рядок. */
    private final JLabel statusLabel = new JLabel("Готово");
    /** Таблиця БД, потрібна для вибору записів CRUD. */
    private JTable databaseTable;

    /**
     * Створює головне вікно та всі вкладки.
     *
     * @param manager менеджер агрегованого масиву
     * @param repository JDBC-репозиторій
     * @param textFile вхідний текстовий файл ЛР10
     * @param binaryFile двійковий файл ЛР10
     * @param apiIndex головна сторінка згенерованої API-документації
     */
    public MainFrame(
            InstitutionArrayManager manager,
            InstitutionRepository repository,
            Path textFile,
            Path binaryFile,
            Path apiIndex
    ) {
        super("Програмування Java · Другий модуль · ЛР8-ЛР11");
        this.manager = manager;
        this.repository = repository;
        this.textFile = textFile;
        this.binaryFile = binaryFile;
        this.apiIndex = apiIndex;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(980, 650));
        setSize(1180, 760);
        setLocationRelativeTo(null);
        setContentPane(createContent());
        refreshArrayViews();
        refreshDatabaseView();
    }

    /**
     * Формує кореневу панель із заголовком, вкладками та статусом.
     *
     * @return готова панель вікна
     */
    private JPanel createContent() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(244, 247, 250));
        root.add(createHeader(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.addTab("ЛР8 · Масив об'єктів", createArrayTab());
        tabs.addTab("ЛР9 · Текст і пошук", createTextTab());
        tabs.addTab("ЛР10 · Файли", createFileTab());
        tabs.addTab("ЛР11 · JDBC", createDatabaseTab());
        root.add(tabs, BorderLayout.CENTER);

        statusLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(205, 214, 223)),
                BorderFactory.createEmptyBorder(7, 14, 7, 14)
        ));
        root.add(statusLabel, BorderLayout.SOUTH);
        return root;
    }

    /**
     * Створює верхній інформаційний блок.
     *
     * @return панель заголовка
     */
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(15, 5));
        header.setBackground(PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("Другий модуль · Фінансова установа");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 23));

        JLabel subtitle = new JLabel("ЛР8–ЛР11: масиви, регулярні вирази, файли та SQLite JDBC");
        subtitle.setForeground(new Color(215, 229, 241));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JPanel text = new JPanel(new GridLayout(0, 1, 0, 3));
        text.setOpaque(false);
        text.add(title);
        text.add(subtitle);

        JButton apiButton = createButton("API документація", ACCENT);
        apiButton.addActionListener(event -> openApiDocumentation());
        header.add(text, BorderLayout.CENTER);
        header.add(apiButton, BorderLayout.EAST);
        return header;
    }

    /**
     * Створює вкладку операцій ЛР8.
     *
     * @return вкладка масиву
     */
    private JPanel createArrayTab() {
        JTable table = createTable(arrayModel);
        JTextField minimumField = new JTextField("15000000", 12);

        JButton showAll = createButton("Показати всі", PRIMARY);
        showAll.addActionListener(event -> {
            updateArrayModel(arrayModel, manager.getAll());
            setStatus("Показано весь агрегований масив.");
        });

        JButton edit = createButton("Редагувати вибраний", ACCENT);
        edit.addActionListener(event -> editSelectedArrayItem(table));

        JButton filter = createButton("Капітал ≥", PRIMARY);
        filter.addActionListener(event -> runUiAction(() -> {
            double minimum = parseNonNegativeDouble(minimumField.getText());
            FinancialInstitution[] result = manager.filterByMinimumCapital(minimum);
            updateArrayModel(arrayModel, result);
            setStatus("ЛР8: знайдено об'єктів — " + result.length + ".");
        }));

        JButton ascending = createButton("Капітал ↑", PRIMARY);
        ascending.addActionListener(event -> {
            manager.sortByCapitalAscending();
            refreshArrayViews();
            setStatus("Масив відсортовано за зростанням капіталу.");
        });

        JButton descending = createButton("Капітал ↓", PRIMARY);
        descending.addActionListener(event -> {
            manager.sortByCapitalDescending();
            refreshArrayViews();
            setStatus("Масив відсортовано за спаданням капіталу.");
        });

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        controls.add(showAll);
        controls.add(edit);
        controls.add(new JLabel("Мінімум:"));
        controls.add(minimumField);
        controls.add(filter);
        controls.add(ascending);
        controls.add(descending);

        return createTabPanel(
                "ЛР8 · Робота з агрегованим масивом об'єктів",
                "Редагування, числова умова та сортування за базовим капіталом.",
                table,
                controls
        );
    }

    /**
     * Створює вкладку операцій ЛР9.
     *
     * @return вкладка текстового пошуку
     */
    private JPanel createTextTab() {
        JTable table = createTable(textModel);
        JTextField regexField = new JTextField(".*банк$", 18);

        JButton showAll = createButton("Показати всі", PRIMARY);
        showAll.addActionListener(event -> updateArrayModel(textModel, manager.getAll()));

        JButton ascending = createButton("Назва А→Я", PRIMARY);
        ascending.addActionListener(event -> {
            manager.sortByNameAscending();
            refreshArrayViews();
            setStatus("Назви відсортовано за українським алфавітом.");
        });

        JButton descending = createButton("Назва Я→А", PRIMARY);
        descending.addActionListener(event -> {
            manager.sortByNameDescending();
            refreshArrayViews();
            setStatus("Назви відсортовано у зворотному напрямку.");
        });

        JButton regex = createButton("Застосувати regex", ACCENT);
        regex.addActionListener(event -> runUiAction(() -> {
            FinancialInstitution[] result = manager.filterByNameRegex(regexField.getText());
            updateArrayModel(textModel, result);
            setStatus("ЛР9: регулярному виразу відповідає об'єктів — " + result.length + ".");
        }));

        JButton extremes = createButton("Мінімум / максимум", ACCENT);
        extremes.addActionListener(event -> showCapitalExtremes());

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        controls.add(showAll);
        controls.add(ascending);
        controls.add(descending);
        controls.add(new JLabel("Вираз:"));
        controls.add(regexField);
        controls.add(regex);
        controls.add(extremes);

        return createTabPanel(
                "ЛР9 · Текстове сортування та регулярні вирази",
                "Умова за замовчуванням: назва закінчується словом «Банк».",
                table,
                controls
        );
    }

    /**
     * Створює вкладку файлових операцій ЛР10.
     *
     * @return вкладка роботи з файлами
     */
    private JPanel createFileTab() {
        JTable table = createTable(fileModel);
        fileLog.setEditable(false);
        fileLog.setRows(5);
        fileLog.setFont(new Font("Consolas", Font.PLAIN, 12));
        fileLog.setText("Текстовий файл: " + textFile.toAbsolutePath().normalize()
                + "\nДвійковий файл: " + binaryFile.toAbsolutePath().normalize());

        JButton loadText = createButton("Зчитати TXT", PRIMARY);
        loadText.addActionListener(event -> loadTextFile());
        JButton writeBinary = createButton("Записати DAT", ACCENT);
        writeBinary.addActionListener(event -> writeBinaryFile());
        JButton readBinary = createButton("Зчитати DAT", PRIMARY);
        readBinary.addActionListener(event -> readBinaryFile());

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        controls.add(loadText);
        controls.add(writeBinary);
        controls.add(readBinary);

        JPanel center = new JPanel(new BorderLayout(0, 8));
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        JScrollPane logScroll = new JScrollPane(fileLog);
        logScroll.setPreferredSize(new Dimension(100, 105));
        center.add(logScroll, BorderLayout.SOUTH);

        return createTabPanel(
                "ЛР10 · Текстові та двійкові файли",
                "TXT формує масив; DAT зберігає відсортовані типізовані записи.",
                center,
                controls,
                false
        );
    }

    /**
     * Створює вкладку JDBC-клієнта ЛР11.
     *
     * @return вкладка бази даних
     */
    private JPanel createDatabaseTab() {
        databaseTable = createTable(databaseModel);
        JTextField minimumField = new JTextField("20000000", 12);

        JButton refresh = createButton("Оновити", PRIMARY);
        refresh.addActionListener(event -> refreshDatabaseView());
        JButton add = createButton("Додати", ACCENT);
        add.addActionListener(event -> addDatabaseItem());
        JButton edit = createButton("Редагувати", PRIMARY);
        edit.addActionListener(event -> editDatabaseItem());
        JButton delete = createButton("Видалити", new Color(160, 55, 55));
        delete.addActionListener(event -> deleteDatabaseItem());
        JButton filter = createButton("Вибірка ≥", PRIMARY);
        filter.addActionListener(event -> filterDatabase(minimumField.getText()));

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        controls.add(refresh);
        controls.add(add);
        controls.add(edit);
        controls.add(delete);
        controls.add(new JLabel("Капітал:"));
        controls.add(minimumField);
        controls.add(filter);

        return createTabPanel(
                "ЛР11 · SQLite JDBC клієнт",
                "Повний CRUD і параметризована вибірка з однієї таблиці БД.",
                databaseTable,
                controls
        );
    }

    /**
     * Створює стандартне оформлення вкладки з таблицею.
     *
     * @param title заголовок
     * @param description короткий опис
     * @param table таблиця вкладки
     * @param controls панель кнопок
     * @return готова вкладка
     */
    private JPanel createTabPanel(
            String title,
            String description,
            JTable table,
            JPanel controls
    ) {
        return createTabPanel(title, description, new JScrollPane(table), controls, false);
    }

    /**
     * Створює стандартне оформлення вкладки з довільним центром.
     *
     * @param title заголовок
     * @param description короткий опис
     * @param center центральний компонент
     * @param controls панель кнопок
     * @param alreadyWrapped ознака того, що центр уже містить прокрутку
     * @return готова вкладка
     */
    private JPanel createTabPanel(
            String title,
            String description,
            java.awt.Component center,
            JPanel controls,
            boolean alreadyWrapped
    ) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(14, 16, 16, 16));

        JLabel heading = new JLabel("<html><b>" + title + "</b><br><span style='color:#566573'>"
                + description + "</span></html>");
        heading.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        heading.setBorder(BorderFactory.createEmptyBorder(0, 2, 3, 2));

        JPanel north = new JPanel(new BorderLayout());
        north.add(heading, BorderLayout.NORTH);
        north.add(controls, BorderLayout.SOUTH);
        panel.add(north, BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Створює таблицю з єдиним вибором рядка.
     *
     * @param model модель даних
     * @return налаштована JTable
     */
    private JTable createTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setFillsViewportHeight(true);
        return table;
    }

    /**
     * Створює модель таблиці, яку користувач не редагує безпосередньо.
     *
     * @param databaseMode чи потрібна колонка ID
     * @return порожня модель
     */
    private static DefaultTableModel createReadOnlyModel(boolean databaseMode) {
        String[] columns = databaseMode
                ? new String[] {"ID", "Назва", "Ліцензія", "Базовий капітал, грн"}
                : new String[] {"№", "Назва", "Ліцензія", "Базовий капітал, грн"};
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    /**
     * Створює стилізовану кнопку.
     *
     * @param text напис
     * @param color колір фону
     * @return JButton
     */
    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return button;
    }

    /** Оновлює всі три представлення масиву ЛР8-ЛР10. */
    private void refreshArrayViews() {
        FinancialInstitution[] all = manager.getAll();
        updateArrayModel(arrayModel, all);
        updateArrayModel(textModel, all);
        updateArrayModel(fileModel, all);
    }

    /**
     * Заповнює модель таблиці даними масиву.
     *
     * @param model цільова модель
     * @param institutions об'єкти для показу
     */
    private void updateArrayModel(
            DefaultTableModel model,
            FinancialInstitution[] institutions
    ) {
        model.setRowCount(0);
        for (int i = 0; i < institutions.length; i++) {
            FinancialInstitution item = institutions[i];
            model.addRow(new Object[] {
                i,
                item.getName(),
                item.getLicenseCode(),
                formatNumber(item.getBaseCapital())
            });
        }
    }

    /** Оновлює таблицю JDBC всіма записами. */
    private void refreshDatabaseView() {
        runUiAction(() -> {
            updateDatabaseModel(repository.findAll());
            setStatus("Таблицю БД оновлено.");
        });
    }

    /**
     * Заповнює таблицю JDBC списком записів.
     *
     * @param institutions записи БД
     */
    private void updateDatabaseModel(List<FinancialInstitution> institutions) {
        databaseModel.setRowCount(0);
        for (FinancialInstitution item : institutions) {
            databaseModel.addRow(new Object[] {
                item.getId(),
                item.getName(),
                item.getLicenseCode(),
                formatNumber(item.getBaseCapital())
            });
        }
    }

    /**
     * Відкриває форму редагування вибраного об'єкта масиву.
     *
     * @param table таблиця вкладки ЛР8
     */
    private void editSelectedArrayItem(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            showInformation("Спочатку виберіть рядок таблиці.");
            return;
        }
        String currentLicense = table.getValueAt(row, 2).toString();
        FinancialInstitution existing = new FinancialInstitution(
                table.getValueAt(row, 1).toString(),
                currentLicense,
                parseFormattedNumber(table.getValueAt(row, 3).toString())
        );
        FinancialInstitution replacement = showInstitutionDialog(
                "Редагування об'єкта масиву", existing);
        if (replacement != null) {
            runUiAction(() -> {
                manager.updateByLicense(currentLicense, replacement);
                refreshArrayViews();
                setStatus("Об'єкт масиву успішно змінено.");
            });
        }
    }

    /** Показує повідомлення про мінімальний та максимальний капітал. */
    private void showCapitalExtremes() {
        CapitalExtremes extremes = manager.findCapitalExtremes();
        FinancialInstitution minimum = extremes.getMinimum();
        FinancialInstitution maximum = extremes.getMaximum();
        String message = "Найменший базовий капітал "
                + formatNumber(minimum.getBaseCapital()) + " грн має «" + minimum.getName()
                + "».\nНайбільший — " + formatNumber(maximum.getBaseCapital())
                + " грн — має «" + maximum.getName() + "».";
        JOptionPane.showMessageDialog(this, message, "Екстремальні значення",
                JOptionPane.INFORMATION_MESSAGE);
        setStatus("Визначено мінімальний і максимальний капітал.");
    }

    /** Зчитує початковий текстовий файл у спільний масив. */
    private void loadTextFile() {
        runUiAction(() -> {
            manager.replaceAll(InstitutionFileService.readText(textFile));
            refreshArrayViews();
            appendFileLog("TXT успішно прочитано: " + textFile.toAbsolutePath().normalize());
            setStatus("ЛР10: масив завантажено з TXT.");
        });
    }

    /** Сортує за назвою та записує спільний масив у DAT. */
    private void writeBinaryFile() {
        runUiAction(() -> {
            manager.sortByNameAscending();
            InstitutionFileService.writeBinary(binaryFile, manager.getAll());
            refreshArrayViews();
            appendFileLog("DAT записано: " + binaryFile.toAbsolutePath().normalize()
                    + " (" + Files.size(binaryFile) + " байт)");
            setStatus("ЛР10: відсортований масив записано у DAT.");
        });
    }

    /** Зчитує DAT та замінює спільний масив відновленими об'єктами. */
    private void readBinaryFile() {
        runUiAction(() -> {
            manager.replaceAll(InstitutionFileService.readBinary(binaryFile));
            refreshArrayViews();
            appendFileLog("DAT прочитано; об'єкти відновлено на екрані.");
            setStatus("ЛР10: масив відновлено з DAT.");
        });
    }

    /** Відкриває діалог додавання запису БД. */
    private void addDatabaseItem() {
        FinancialInstitution value = showInstitutionDialog("Новий запис БД", null);
        if (value != null) {
            runUiAction(() -> {
                int id = repository.add(value);
                refreshDatabaseView();
                setStatus("До БД додано запис ID = " + id + ".");
            });
        }
    }

    /** Відкриває діалог редагування вибраного запису БД. */
    private void editDatabaseItem() {
        int row = databaseTable.getSelectedRow();
        if (row < 0) {
            showInformation("Спочатку виберіть запис БД.");
            return;
        }
        int id = ((Number) databaseTable.getValueAt(row, 0)).intValue();
        FinancialInstitution existing = new FinancialInstitution(
                id,
                databaseTable.getValueAt(row, 1).toString(),
                databaseTable.getValueAt(row, 2).toString(),
                parseFormattedNumber(databaseTable.getValueAt(row, 3).toString())
        );
        FinancialInstitution replacement = showInstitutionDialog("Редагування запису БД", existing);
        if (replacement != null) {
            replacement.setId(id);
            runUiAction(() -> {
                if (!repository.update(replacement)) {
                    throw new SQLException("Запис із ID " + id + " не знайдено.");
                }
                refreshDatabaseView();
                setStatus("Запис ID = " + id + " оновлено.");
            });
        }
    }

    /** Після підтвердження видаляє вибраний запис БД. */
    private void deleteDatabaseItem() {
        int row = databaseTable.getSelectedRow();
        if (row < 0) {
            showInformation("Спочатку виберіть запис БД.");
            return;
        }
        int id = ((Number) databaseTable.getValueAt(row, 0)).intValue();
        String name = databaseTable.getValueAt(row, 1).toString();
        int answer = JOptionPane.showConfirmDialog(
                this,
                "Видалити запис «" + name + "» (ID = " + id + ")?",
                "Підтвердження видалення",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (answer == JOptionPane.YES_OPTION) {
            runUiAction(() -> {
                repository.delete(id);
                refreshDatabaseView();
                setStatus("Запис ID = " + id + " видалено.");
            });
        }
    }

    /**
     * Виконує параметризовану вибірку БД.
     *
     * @param text текст мінімального капіталу
     */
    private void filterDatabase(String text) {
        runUiAction(() -> {
            double minimum = parseNonNegativeDouble(text);
            List<FinancialInstitution> result = repository.findByMinimumCapital(minimum);
            updateDatabaseModel(result);
            setStatus("ЛР11: вибірка містить записів — " + result.size() + ".");
        });
    }

    /**
     * Показує універсальний діалог введення установи.
     *
     * @param title заголовок діалогу
     * @param existing наявний об'єкт або {@code null}
     * @return новий об'єкт або {@code null} після скасування
     */
    private FinancialInstitution showInstitutionDialog(
            String title,
            FinancialInstitution existing
    ) {
        JTextField name = new JTextField(existing == null ? "" : existing.getName(), 28);
        JTextField license = new JTextField(
                existing == null ? "" : existing.getLicenseCode(), 28);
        JTextField capital = new JTextField(
                existing == null ? "" : Double.toString(existing.getBaseCapital()), 28);

        JPanel form = new JPanel(new GridLayout(0, 1, 5, 4));
        form.add(new JLabel("Назва:"));
        form.add(name);
        form.add(new JLabel("Код ліцензії:"));
        form.add(license);
        form.add(new JLabel("Базовий капітал, грн:"));
        form.add(capital);

        int answer = JOptionPane.showConfirmDialog(
                this, form, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (answer != JOptionPane.OK_OPTION) {
            return null;
        }
        try {
            int id = existing == null ? 0 : existing.getId();
            return new FinancialInstitution(
                    id,
                    name.getText(),
                    license.getText(),
                    parseNonNegativeDouble(capital.getText())
            );
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
            return null;
        }
    }

    /** Відкриває згенеровану HTML API-документацію у браузері. */
    private void openApiDocumentation() {
        runUiAction(() -> {
            if (!Files.exists(apiIndex)) {
                throw new IOException("Спочатку виконайте generate-docs.bat.");
            }
            if (!Desktop.isDesktopSupported()) {
                throw new IOException("Відкриття браузера не підтримується системою.");
            }
            Desktop.getDesktop().browse(apiIndex.toUri());
            setStatus("Відкрито API-документацію.");
        });
    }

    /**
     * Виконує дію та централізовано показує помилки користувачу.
     *
     * @param action дія, що може завершитися винятком
     */
    private void runUiAction(UiAction action) {
        try {
            action.run();
        } catch (PatternSyntaxException e) {
            showError("Помилка регулярного виразу: " + e.getDescription());
        } catch (SQLException e) {
            showError(databaseErrorMessage(e));
        } catch (Exception e) {
            showError(e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
        }
    }

    /**
     * Перетворює текст на невід'ємне число.
     *
     * @param text введення користувача
     * @return числове значення
     */
    private double parseNonNegativeDouble(String text) {
        try {
            double value = Double.parseDouble(text.trim().replace(',', '.'));
            if (Double.isFinite(value) && value >= 0) {
                return value;
            }
        } catch (NumberFormatException ignored) {
            // Нижче формується єдине зрозуміле повідомлення.
        }
        throw new IllegalArgumentException("Введіть невід'ємне число.");
    }

    /**
     * Розбирає відформатоване число з таблиці.
     *
     * @param text число з пробілами та комою
     * @return значення double
     */
    private double parseFormattedNumber(String text) {
        String normalized = text.replace("\u00A0", "")
                .replace(" ", "")
                .replace(',', '.');
        return Double.parseDouble(normalized);
    }

    /**
     * Форматує число за українськими правилами.
     *
     * @param value число
     * @return текст із двома десятковими знаками
     */
    private String formatNumber(double value) {
        NumberFormat format = NumberFormat.getNumberInstance(UKRAINIAN_LOCALE);
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        return format.format(value);
    }

    /**
     * Перетворює технічну помилку SQLite на зрозумілий текст.
     *
     * @param exception SQLException
     * @return повідомлення для користувача
     */
    private String databaseErrorMessage(SQLException exception) {
        String message = exception.getMessage();
        if (message != null && message.contains("UNIQUE constraint failed")) {
            return "Код ліцензії вже використовується іншим записом.";
        }
        if (message != null && message.contains("CHECK constraint failed")) {
            return "Порушено обмеження цілісності даних.";
        }
        return message == null ? exception.getClass().getSimpleName() : message;
    }

    /**
     * Додає рядок до журналу файлової вкладки.
     *
     * @param message повідомлення
     */
    private void appendFileLog(String message) {
        fileLog.append("\n" + message);
        fileLog.setCaretPosition(fileLog.getDocument().getLength());
    }

    /**
     * Змінює текст нижнього статусного рядка.
     *
     * @param message новий статус
     */
    private void setStatus(String message) {
        statusLabel.setText(message);
    }

    /**
     * Показує інформаційне повідомлення.
     *
     * @param message текст
     */
    private void showInformation(String message) {
        JOptionPane.showMessageDialog(this, message, "Інформація",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Показує повідомлення про помилку.
     *
     * @param message текст помилки
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Помилка",
                JOptionPane.ERROR_MESSAGE);
        setStatus("Помилка: " + message);
    }

    /** Функціональний контракт GUI-дії з перевірюваними винятками. */
    @FunctionalInterface
    private interface UiAction {
        /**
         * Виконує дію.
         *
         * @throws Exception якщо дія не вдалася
         */
        void run() throws Exception;
    }
}
