/*
 * Теоретичні відповіді:
 *
 * Різниця між звичайним та абстрактним класом:
 * - Екземпляри звичайного класу можна створювати безпосередньо через new. Екземпляри абстрактного
 *   класу створити не можна — він слугує базовим шаблоном для нащадків.
 * - Абстрактний клас може містити абстрактні методи (без тіла), які нащадки зобов'язані реалізувати.
 *   Звичайний клас не може містити абстрактних методів.
 *
 * Різниця між абстрактним класом та інтерфейсом:
 * - Спадкування: клас може успадкувати лише один абстрактний клас (single inheritance), але може
 *   імплементувати необмежену кількість інтерфейсів (multiple implementation).
 * - Стан (поля): абстрактний клас може мати звичайні змінні екземпляра (будь-яких модифікаторів),
 *   конструктори та змінюваний стан. В інтерфейсі всі змінні за замовчуванням є public static final
 *   (константами), а конструктори відсутні.
 * - Призначення: абстрактний клас описує родинну ієрархію сутностей ("is-a"), тоді як інтерфейс
 *   визначає контракт поведінки ("can-do").
 *
 * Кратне (множинне) успадкування в Java:
 * - Множинне успадкування класів (через extends) у Java заборонено, щоб уникнути проблеми ромба (diamond problem).
 * - Реалізується за допомогою інтерфейсів: клас може імплементувати декілька інтерфейсів одночасно (implements A, B, C).
 * - З Java 8 інтерфейси підтримують default-методи з готовою реалізацією, що дозволяє комбінувати поведінку кількох джерел.
 */

interface Auditable {
    void conductAudit();
    double calculateTaxObligation();
}

interface Insurable {
    void registerInsurancePolicy(String policyId, double coverageAmount);
}

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

abstract class FinancialInstitution implements Auditable {
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

    public abstract void describeActivity();

    public abstract double calculateAnnualRiskReserve();

    @Override
    public void conductAudit() {
        System.out.printf("Аудит: перевірка ліцензії %s для установи '%s' проведена успішно.\n", licenseCode, name);
    }
}

class Bank extends FinancialInstitution implements Insurable {
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
        System.out.printf("Резервний фонд:     %.2f грн\n", calculateAnnualRiskReserve());
        System.out.printf("Податкове зобов'яз: %.2f грн\n", calculateTaxObligation());
    }

    @Override
    public void describeActivity() {
        System.out.println("Діяльність банку: Розрахунково-касове обслуговування, кредитування, депозити.");
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
        System.out.printf("Страхування депозитів: поліс %s на суму %.2f грн зареєстровано.\n", policyId, coverageAmount);
    }
}

class InsuranceCompany extends FinancialInstitution implements Insurable {
    private double insuredRiskPool;

    public InsuranceCompany() {
        super("Оранта-Гарант", "LIC-55001", 15_000_000.0);
        this.insuredRiskPool = 50_000_000.0;
    }

    public InsuranceCompany(String name, String licenseCode, double baseCapital, double insuredRiskPool) {
        super(name, licenseCode, baseCapital);
        this.insuredRiskPool = insuredRiskPool;
    }

    public double getInsuredRiskPool() {
        return insuredRiskPool;
    }

    public void setInsuredRiskPool(double insuredRiskPool) {
        this.insuredRiskPool = insuredRiskPool;
    }

    @Override
    public void showInfo() {
        super.showInfo();
        System.out.printf("Страховий пул ризиків: %.2f грн\n", insuredRiskPool);
        System.out.printf("Резервний фонд:        %.2f грн\n", calculateAnnualRiskReserve());
        System.out.printf("Податкове зобов'язання: %.2f грн\n", calculateTaxObligation());
    }

    @Override
    public void describeActivity() {
        System.out.println("Діяльність страхової компанії: Перестрахування, майновий захист, страхування життя.");
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
        System.out.printf("Випущено страховий поліс %s на суму %.2f грн. Новий пул: %.2f грн\n",
                policyId, coverageAmount, this.insuredRiskPool);
    }
}

public class Main {
    public static void main(String[] args) {
        Account[] clientAccounts = {
            new Account("UA26300123", 45000.50),
            new Account("UA26300456", 120500.00),
            new Account("UA26300789", 15200.25)
        };

        Bank bank = new Bank("АгроПромінвест Банк", "LIC-140514", 25_000_000.0, "AGROUA2X", clientAccounts);
        InsuranceCompany insurer = new InsuranceCompany("Аска-Страхування", "LIC-889900", 12_000_000.0, 40_000_000.0);

        FinancialInstitution[] institutions = {bank, insurer};
        for (FinancialInstitution fi : institutions) {
            fi.showInfo();
            fi.describeActivity();
            fi.conductAudit();
            System.out.println();
        }

        Insurable[] insurableEntities = {bank, insurer};
        insurableEntities[0].registerInsurancePolicy("POL-DEPOSIT-01", 500_000.0);
        insurableEntities[1].registerInsurancePolicy("POL-AGRO-77", 2_000_000.0);
    }
}
