/*
 * Базові принципи ООП:
 * - Інкапсуляція: приховування внутрішнього стану та надання доступу через публічний інтерфейс.
 * - Успадкування: повторне використання та розширення коду базового класу в класі-нащадку.
 * - Поліморфізм: виконання різної поведінки для однакового інтерфейсу виклику.
 * - Абстракція: фокусування на суттєвих характеристиках без прив'язки до деталей реалізації.
 *
 * Перевантаження та перевизначення методів:
 * - Перевантаження (overloading): методи з однаковим ім'ям, але різними параметрами в одному класі (статичний поліморфізм).
 * - Перевизначення (overriding): зміна реалізації методу батьківського класу в нащадку з збереженням сигнатури (динамічний поліморфізм).
 *
 * Агрегація та композиція:
 * - Агрегація ("has-a"): слабкий зв'язок; об'єкти існують незалежно (банк і банківські рахунки / співробітники).
 * - Композиція ("part-of"): сильний зв'язок; життєвий цикл залежного об'єкта нерозривний з батьківським (будинок і кімнати).
 *
 * Специфікатори private vs protected:
 * - private: доступний виключно в межах класу, в якому оголошений.
 * - protected: доступний у межах класу, його пакету та у всіх класах-нащадках.
 *
 * Ключові слова super та this:
 * - this: посилання на поточний екземпляр класу.
 * - super: звернення до членів або конструктора батьківського класу.
 */

class Account {
    private String accountNumber;
    private double balance;

    public Account() {
        this("UA00000000", 0.0);
    }

    public Account(String accountNumber, double balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void showInfo() {
        System.out.printf("   [Рахунок] %-12s | Баланс: %.2f грн\n", accountNumber, balance);
    }
}

class FinancialInstitution {
    protected String name;
    protected String licenseCode;
    protected double baseCapital;

    public FinancialInstitution() {
        this("Невідома фінансова установа", "LIC-00000", 1_000_000.0);
    }

    public FinancialInstitution(String name, String licenseCode, double baseCapital) {
        this.name = name;
        this.licenseCode = licenseCode;
        this.baseCapital = baseCapital;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLicenseCode() {
        return licenseCode;
    }

    public void setLicenseCode(String licenseCode) {
        this.licenseCode = licenseCode;
    }

    public double getBaseCapital() {
        return baseCapital;
    }

    public void setBaseCapital(double baseCapital) {
        this.baseCapital = baseCapital;
    }

    public void showInfo() {
        System.out.println("Фінансова установа: " + name);
        System.out.println("Ліцензія:           " + licenseCode);
        System.out.printf("Капітал:            %.2f грн\n", baseCapital);
    }

    public void describeActivity() {
        System.out.println("Діяльність: Надання загальних фінансових послуг.");
    }

    public void describeActivity(String sector) {
        System.out.println("Діяльність: Спеціалізовані послуги у секторі: " + sector);
    }
}

class Bank extends FinancialInstitution {
    private String swiftCode;
    private Account[] accounts;

    public Bank() {
        super("Національний Стандарт Банк", "LIC-10001", 10_000_000.0);
        this.swiftCode = "NSTDUA2X";
        this.accounts = new Account[0];
    }

    public Bank(String name, String licenseCode, double baseCapital, String swiftCode, Account[] accounts) {
        super(name, licenseCode, baseCapital);
        this.swiftCode = swiftCode;
        if (accounts != null) {
            this.accounts = new Account[accounts.length];
            System.arraycopy(accounts, 0, this.accounts, 0, accounts.length);
        } else {
            this.accounts = new Account[0];
        }
    }

    public String getSwiftCode() {
        return swiftCode;
    }

    public void setSwiftCode(String swiftCode) {
        this.swiftCode = swiftCode;
    }

    public Account[] getAccounts() {
        return accounts;
    }

    public void setAccounts(Account[] accounts) {
        this.accounts = accounts;
    }

    public double getTotalAccountsBalance() {
        double total = 0.0;
        for (Account acc : accounts) {
            total += acc.getBalance();
        }
        return total;
    }

    @Override
    public void showInfo() {
        super.showInfo();
        System.out.println("SWIFT:              " + swiftCode);
        System.out.println("Кількість рахунків: " + accounts.length);
        if (accounts.length == 0) {
            System.out.println("   (Немає відкритих рахунків)");
        } else {
            for (Account acc : accounts) {
                acc.showInfo();
            }
        }
        System.out.printf("Баланс рахунків:    %.2f грн\n", getTotalAccountsBalance());
    }

    @Override
    public void describeActivity() {
        System.out.println("Діяльність банку: Розрахунково-касове обслуговування, кредитування, депозити.");
    }

    public void describeActivity(String sector, boolean isOnline) {
        String channel = isOnline ? "онлайн-банкінг" : "відділення";
        System.out.printf("Діяльність банку: Послуги в секторі '%s' (%s).\n", sector, channel);
    }
}

public class Main {
    public static void main(String[] args) {
        Account accDefault = new Account();
        accDefault.showInfo();

        Account acc1 = new Account("UA26300123", 45000.50);
        Account acc2 = new Account("UA26300456", 120500.00);
        Account acc3 = new Account("UA26300789", 15200.25);

        FinancialInstitution institution = new FinancialInstitution("ДержФінКонтроль", "LIC-778899", 5_000_000.0);
        institution.showInfo();
        institution.describeActivity();
        institution.describeActivity("Мікрокредитування");

        Account[] clientAccounts = {acc1, acc2, acc3};
        Bank bank = new Bank("АгроПромінвест Банк", "LIC-140514", 25_000_000.0, "AGROUA2X", clientAccounts);
        bank.showInfo();

        bank.describeActivity();
        bank.describeActivity("Корпоративні клієнти");
        bank.describeActivity("Роздрібні клієнти", true);

        FinancialInstitution polyRef = bank;
        polyRef.showInfo();
    }
}
