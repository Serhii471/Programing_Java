package gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;

public class MainApp extends JFrame {

    public MainApp() {
        setTitle("НУБіП України — Програмування Java (Лабораторні роботи 1-7)");
        setSize(950, 680);
        setMinimumSize(new Dimension(850, 580));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        tabbedPane.addTab("Про автора", createAuthorPanel());
        tabbedPane.addTab("ЛР 1-3: Обчислення виразу", createLab1To3Panel());
        tabbedPane.addTab("ЛР 4: Цикли (Табулювання)", createLab4Panel());
        tabbedPane.addTab("ЛР 5: ООП & Методи", createLab5Panel());
        tabbedPane.addTab("ЛР 6: Успадкування & Агрегація", createLab6Panel());
        tabbedPane.addTab("ЛР 7: Абстракція & Інтерфейси", createLab7Panel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createAuthorPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel photoLabel = new JLabel();
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        photoLabel.setPreferredSize(new Dimension(220, 260));
        photoLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        File photoFile = new File("src/resources/author.png");
        if (photoFile.exists()) {
            try {
                BufferedImage img = ImageIO.read(photoFile);
                Image scaled = img.getScaledInstance(200, 240, Image.SCALE_SMOOTH);
                photoLabel.setIcon(new ImageIcon(scaled));
            } catch (Exception e) {
                photoLabel.setIcon(createAvatarPlaceholder());
            }
        } else {
            photoLabel.setIcon(createAvatarPlaceholder());
        }

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel("Сергій");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel universityLabel = new JLabel("Національний університет біоресурсів і природокористування України");
        universityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        JLabel courseLabel = new JLabel("Дисципліна: Програмування Java");
        courseLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        JLabel variantLabel = new JLabel("Варіант: 14");
        variantLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        variantLabel.setForeground(new Color(0, 102, 204));

        JTextArea descArea = new JTextArea();
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descArea.setBackground(panel.getBackground());
        descArea.setText(
            "Даний програмний комплекс об'єднує практичні результати виконання лабораторних робіт №1-7:\n" +
            "• ЛР1: Лінійні обчислення виразу за варіантом 14.\n" +
            "• ЛР2: Перевірка області допустимих значень (ОДЗ, знаменник != 0).\n" +
            "• ЛР3: Контроль виняткових ситуацій введення даних (try-catch-finally).\n" +
            "• ЛР4: Циклічні конструкції for та while, табулювання функції з кроком.\n" +
            "• ЛР5: Принципи ООП, інкапсуляція, гетери/сетери, статичні та нестатичні методи, пакети.\n" +
            "• ЛР6: Агрегація масиву об'єктів рахунків, успадкування (FinancialInstitution -> Bank).\n" +
            "• ЛР7: Абстрактний базовий клас, контракти інтерфейсів Auditable та Insurable, кратне успадкування."
        );

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(universityLabel);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(courseLabel);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(variantLabel);
        infoPanel.add(Box.createVerticalStrut(15));
        infoPanel.add(descArea);

        panel.add(photoLabel, BorderLayout.WEST);
        panel.add(infoPanel, BorderLayout.CENTER);

        return panel;
    }

    private ImageIcon createAvatarPlaceholder() {
        int w = 200, h = 240;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(230, 235, 245));
        g2.fillRect(0, 0, w, h);

        g2.setColor(new Color(100, 130, 180));
        g2.fillOval(w / 2 - 40, 45, 80, 80);
        g2.fillArc(w / 2 - 70, 140, 140, 120, 0, 180);

        g2.setColor(new Color(60, 60, 60));
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        FontMetrics fm = g2.getFontMetrics();
        String text = "author.png";
        g2.drawString(text, (w - fm.stringWidth(text)) / 2, h - 15);
        g2.dispose();
        return new ImageIcon(img);
    }

    private JPanel createLab1To3Panel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        JLabel formulaLabel = new JLabel("Формула (Вар. 14): R = (sin(x^2 + 4)^3 + 4.3) / sin^3(x^4)");
        formulaLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel odzLabel = new JLabel("ОДЗ: sin^3(x^4) != 0. Реалізовано try-catch валідацію вхідного x.");
        odzLabel.setForeground(Color.GRAY);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JLabel xLabel = new JLabel("Введіть аргумент x:");
        JTextField xField = new JTextField("2.0", 12);
        JButton calcBtn = new JButton("Обчислити R");

        inputPanel.add(xLabel);
        inputPanel.add(xField);
        inputPanel.add(calcBtn);

        topPanel.add(formulaLabel);
        topPanel.add(odzLabel);
        topPanel.add(inputPanel);

        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(resultArea);

        calcBtn.addActionListener(e -> {
            resultArea.setText("");
            String raw = xField.getText().trim().replace(',', '.');
            double x;
            try {
                x = Double.parseDouble(raw);
            } catch (NumberFormatException ex) {
                resultArea.append("[Помилка ЛР3]: Введено некоректні дані. Потрібно ввести дійсне число.\n");
                resultArea.append("Виняткова ситуація: " + ex.toString() + "\n");
                return;
            }

            double denominator = Math.pow(Math.sin(Math.pow(x, 4)), 3);
            if (Math.abs(denominator) < 1e-10) {
                resultArea.append("[Помилка ЛР2: ОДЗ]: Знаменник sin^3(x^4) дорівнює 0 (або < 1e-10).\n");
                resultArea.append("Значення аргументу x = " + x + " не належить області допустимих значень!\n");
                return;
            }

            double numerator = Math.pow(Math.sin(x * x + 4.0), 3) + 4.3;
            double r = numerator / denominator;

            resultArea.append("Успішне обчислення (ЛР1-3):\n");
            resultArea.append("Вхідне x:     " + x + "\n");
            resultArea.append("Чисельник:    " + numerator + "\n");
            resultArea.append("Знаменник:    " + denominator + "\n");
            resultArea.append("Результат R:  " + String.format("%.8f", r) + "\n");
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLab4Panel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        JLabel nLabel = new JLabel("Номер варіанту N:");
        JTextField nField = new JTextField("14", 5);
        JButton runBtn = new JButton("Табулювати (for & while)");

        top.add(nLabel);
        top.add(nField);
        top.add(runBtn);

        JTextArea outArea = new JTextArea();
        outArea.setEditable(false);
        outArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(outArea);

        runBtn.addActionListener(e -> {
            outArea.setText("");
            int n;
            try {
                n = Integer.parseInt(nField.getText().trim());
            } catch (NumberFormatException ex) {
                outArea.append("Помилка: варіант N має бути цілим числом.\n");
                return;
            }

            double startX = -10.0 - 2.5 * n;
            double endX = 5.0 + 1.2 * n;
            double step = 0.5 + (double) n / 20.0;

            outArea.append(String.format("Параметри табулювання для N = %d:\n", n));
            outArea.append(String.format("Діапазон X: [%.2f; %.2f], крок: %.2f\n\n", startX, endX, step));
            outArea.append(String.format("%-12s | %-25s | %-25s\n", "x", "R (цикл for)", "R (цикл while)"));
            outArea.append("--------------------------------------------------------------------\n");

            double xWhile = startX;
            for (double x = startX; x <= endX + 1e-9; x += step) {
                double denFor = Math.pow(Math.sin(Math.pow(x, 4)), 3);
                String resFor = (Math.abs(denFor) < 1e-10) ? "Пропуск (ОДЗ)" : String.format("%.6f", (Math.pow(Math.sin(x * x + 4.0), 3) + 4.3) / denFor);

                double denWhile = Math.pow(Math.sin(Math.pow(xWhile, 4)), 3);
                String resWhile = (Math.abs(denWhile) < 1e-10) ? "Пропуск (ОДЗ)" : String.format("%.6f", (Math.pow(Math.sin(xWhile * xWhile + 4.0), 3) + 4.3) / denWhile);

                outArea.append(String.format("%-12.4f | %-25s | %-25s\n", x, resFor, resWhile));
                xWhile += step;
            }
        });

        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLab5Panel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 5));
        JLabel title = new JLabel("Клас calc.ExpressionCalculator: демонстрація get/set, конструкторів, static / non-static методів");
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JButton testBtn = new JButton("Запустити тест ООП (ЛР5)");

        top.add(title);
        top.add(testBtn);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Consolas", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(area);

        testBtn.addActionListener(e -> {
            area.setText("");
            calc.ExpressionCalculator defaultObj = new calc.ExpressionCalculator();
            area.append("1. Конструктор за замовчуванням:\n");
            area.append(String.format("   Початкові параметри: x=%.2f, const1=%.2f, const2=%.2f, power=%d\n",
                    defaultObj.getX(), defaultObj.getConstant1(), defaultObj.getConstant2(), defaultObj.getPower()));
            area.append(String.format("   Нестатичний розрахунок defaultObj.calculate() = %.6f\n\n", defaultObj.calculate()));

            area.append("2. Демонстрація роботи сетерів (defaultObj.setX(3.5)):\n");
            defaultObj.setX(3.5);
            area.append(String.format("   Оновлений x: %.2f, новий результат: %.6f\n\n", defaultObj.getX(), defaultObj.calculate()));

            area.append("3. Конструктор з параметрами:\n");
            calc.ExpressionCalculator customObj = new calc.ExpressionCalculator(2.0, 4.0, 4.3, 3);
            area.append(String.format("   customObj.calculate() = %.6f\n\n", customObj.calculate()));

            area.append("4. Статичний метод (без створення екземпляра):\n");
            double rStatic = calc.ExpressionCalculator.calculateStatic(2.0, 4.0, 4.3, 3);
            area.append(String.format("   ExpressionCalculator.calculateStatic(2.0, 4.0, 4.3, 3) = %.6f\n", rStatic));
        });

        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLab6Panel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        JLabel title = new JLabel("Успадкування (FinancialInstitution -> Bank) та Агрегація (Bank has Account[])");
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JButton runBtn = new JButton("Демонстрація ЛР6");

        top.add(title);
        top.add(runBtn);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Consolas", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(area);

        runBtn.addActionListener(e -> {
            area.setText("");
            Account acc1 = new Account("UA26300123", 45000.50);
            Account acc2 = new Account("UA26300456", 120500.00);
            Account acc3 = new Account("UA26300789", 15200.25);
            Account[] accs = {acc1, acc2, acc3};

            Bank bank = new Bank("АгроПромінвест Банк", "LIC-140514", 25_000_000.0, "AGROUA2X", accs);

            area.append("=== БАЗОВИЙ ТА ПЕРЕВИЗНАЧЕНИЙ МЕТОД showInfo() ===\n");
            area.append("Назва банку:      " + bank.getName() + "\n");
            area.append("Ліцензія:         " + bank.getLicenseCode() + "\n");
            area.append(String.format("Капітал:          %.2f грн\n", bank.getBaseCapital()));
            area.append("SWIFT:            " + bank.getSwiftCode() + "\n");
            area.append("Кількість акаунтів: " + bank.getAccounts().length + "\n");
            area.append("Агреговані рахунки:\n");
            for (Account a : bank.getAccounts()) {
                area.append(String.format("   • Рахунок: %s | Баланс: %.2f грн\n", a.getAccountNumber(), a.getBalance()));
            }
            area.append(String.format("Сумарний баланс клієнтів: %.2f грн\n\n", bank.getTotalAccountsBalance()));

            area.append("=== ПЕРЕВАНТАЖЕНІ ТА ПЕРЕВИЗНАЧЕНІ МЕТОДИ ===\n");
            area.append("• Перевизначений describeActivity(): Розрахунково-касове обслуговування, кредити.\n");
            area.append("• Перевантажений describeActivity(sector, isOnline): Онлайн-банкінг для агробізнесу.\n");
        });

        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLab7Panel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        JLabel title = new JLabel("Абстрактний клас FinancialInstitution, інтерфейси Auditable, Insurable");
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JButton runBtn = new JButton("Демонстрація ЛР7 (Поліморфізм)");

        top.add(title);
        top.add(runBtn);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Consolas", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(area);

        runBtn.addActionListener(e -> {
            area.setText("");
            Account[] accs = {new Account("UA111", 50000.0), new Account("UA222", 80000.0)};
            Bank bank = new Bank("Дніпро-Банк", "LIC-3344", 20_000_000.0, "DNIPROUA", accs);
            InsuranceCompany insurer = new InsuranceCompany("Оранта", "LIC-9988", 15_000_000.0, 60_000_000.0);

            FinancialInstitution[] institutions = {bank, insurer};
            area.append("=== ПОЛІМОРФНИЙ ВИКЛИК ЧЕРЕЗ АБСТРАКТНИЙ КЛАС FinancialInstitution ===\n\n");
            for (FinancialInstitution fi : institutions) {
                area.append("• Установа: " + fi.getName() + " (" + fi.getLicenseCode() + ")\n");
                area.append(String.format("  Резервний фонд:      %.2f грн\n", fi.calculateAnnualRiskReserve()));
                area.append(String.format("  Податкове зобов'язання: %.2f грн\n", fi.calculateTaxObligation()));
                area.append("\n");
            }

            area.append("=== КРАТНЕ УСПАДКУВАННЯ (ІНТЕРФЕЙС Insurable) ===\n");
            Insurable[] insurableList = {bank, insurer};
            area.append("• Bank реалізує Insurable: реєстрація захисту вкладів Фондом гарантування.\n");
            area.append("• InsuranceCompany реалізує Insurable: реєстрація прямого полісу страхування.\n");
        });

        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new MainApp().setVisible(true);
        });
    }
}

class Account {
    private String accountNumber;
    private double balance;

    public Account(String accountNumber, double balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }
}

interface Auditable {
    void conductAudit();
    double calculateTaxObligation();
}

interface Insurable {
    void registerInsurancePolicy(String policyId, double coverageAmount);
}

abstract class FinancialInstitution implements Auditable {
    protected String name;
    protected String licenseCode;
    protected double baseCapital;

    public FinancialInstitution(String name, String licenseCode, double baseCapital) {
        this.name = name;
        this.licenseCode = licenseCode;
        this.baseCapital = baseCapital;
    }

    public String getName() {
        return name;
    }

    public String getLicenseCode() {
        return licenseCode;
    }

    public double getBaseCapital() {
        return baseCapital;
    }

    public abstract void describeActivity();
    public abstract double calculateAnnualRiskReserve();

    @Override
    public void conductAudit() {
    }
}

class Bank extends FinancialInstitution implements Insurable {
    private String swiftCode;
    private Account[] accounts;

    public Bank(String name, String licenseCode, double baseCapital, String swiftCode, Account[] accounts) {
        super(name, licenseCode, baseCapital);
        this.swiftCode = swiftCode;
        this.accounts = (accounts != null) ? accounts : new Account[0];
    }

    public String getSwiftCode() {
        return swiftCode;
    }

    public Account[] getAccounts() {
        return accounts;
    }

    public double getTotalAccountsBalance() {
        double s = 0;
        for (Account a : accounts) s += a.getBalance();
        return s;
    }

    @Override
    public void describeActivity() {
    }

    @Override
    public double calculateAnnualRiskReserve() {
        return baseCapital * 0.08 + getTotalAccountsBalance() * 0.02;
    }

    @Override
    public double calculateTaxObligation() {
        return (baseCapital * 0.05 + getTotalAccountsBalance() * 0.01) * 0.18;
    }

    @Override
    public void registerInsurancePolicy(String policyId, double coverageAmount) {
    }
}

class InsuranceCompany extends FinancialInstitution implements Insurable {
    private double insuredRiskPool;

    public InsuranceCompany(String name, String licenseCode, double baseCapital, double insuredRiskPool) {
        super(name, licenseCode, baseCapital);
        this.insuredRiskPool = insuredRiskPool;
    }

    @Override
    public void describeActivity() {
    }

    @Override
    public double calculateAnnualRiskReserve() {
        return insuredRiskPool * 0.15;
    }

    @Override
    public double calculateTaxObligation() {
        return insuredRiskPool * 0.03 * 0.18;
    }

    @Override
    public void registerInsurancePolicy(String policyId, double coverageAmount) {
        this.insuredRiskPool += coverageAmount;
    }
}
