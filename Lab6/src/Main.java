/**
 * ВІДПОВІДІ НА ТЕОРЕТИЧНІ ПИТАННЯ:
 *
 * 1. Базові принципи ООП:
 *    - Інкапсуляція (Encapsulation) — приховування внутрішнього стану (полів) об'єкта від прямого доступу ззовні 
 *      та надання контрольованого доступу через методи (геттери/сеттери).
 *    - Успадкування (Inheritance) — механізм створення нового класу (нащадка/підкласу) на основі вже існуючого 
 *      (батьківського/суперкласу) з успадкуванням його стану та поведінки, що дозволяє повторно використовувати код.
 *    - Поліморфізм (Polymorphism) — здатність об'єктів з однаковим інтерфейсом мати різну реалізацію поведінки 
 *      (реалізується через перевизначення методів @Override або перевантаження).
 *    - Абстракція (Abstraction) — виділення суттєвих характеристик об'єкта, не беручи до уваги другорядні деталі.
 *
 * 2. Що таке перевантаження і перевизначення методів:
 *    - Перевантаження (Overloading) — оголошення в межах одного класу кількох методів з однаковим ім'ям, 
 *      але з різними списками параметрів (кількість, типи або порядок). Визначається на етапі компіляції (статичний поліморфізм).
 *    - Перевизначення (Overriding) — зміна або заміна реалізації методу батьківського класу в класі-нащадку 
 *      з точно такою ж сигнатурою (ім'я, параметри, тип повернення) за допомогою анотації @Override. Визначається під час виконання (динамічний поліморфізм).
 *
 * 3. Поняття агрегації та композиції, приклади:
 *    - Агрегація (Aggregation, відношення "has-a" / "має") — слабкий зв'язок між об'єктами, коли один об'єкт містить посилання 
 *      на інший, але їх життєві цикли незалежні. Дочірній об'єкт створюється ззовні і може продовжувати існувати, якщо батьківський об'єкт буде знищено.
 *      Приклад: Банк і Співробітники (Bank has Employees). Якщо банк закриється, співробітники продовжать існувати і можуть працювати в іншому місці.
 *    - Композиція (Composition, жорстке відношення "part-of" / "частина") — сильний зв'язок, коли складова частина 
 *      не може існувати окремо від цілого об'єкта. Дочірній об'єкт створюється всередині батьківського і знищується разом із ним.
 *      Приклад: Будинок і Кімнати, або Автомобіль і Двигун (у суворій композиції). Якщо знищити будинок, кімнати перестають існувати.
 *
 * 4. Різниця між специфікаторами доступу private і protected:
 *    - private — найсуворіший рівень доступу: член класу (поле або метод) доступний ТІЛЬКИ всередині того самого класу, де він оголошений. 
 *      Класи-нащадки НЕ мають до нього прямого доступу.
 *    - protected — член класу доступний у межах поточного класу, у межах усіх класів того самого пакету, 
 *      а також у всіх класах-нащадках (subclasses), навіть якщо вони знаходяться в інших пакетах.
 *
 * 5. Для чого призначено службові слова super і this:
 *    - this — ключове слово, яке є посиланням на поточний екземпляр класу (використовується для доступу до полів об'єкта `this.field = field`, 
 *      виклику інших конструкторів того ж класу `this()`, або передачі поточного об'єкта у методи).
 *    - super — ключове слово, яке є посиланням на батьківський клас (суперклас). Використовується для виклику конструктора 
 *      батьківського класу `super(...)` або виклику перевизначеного методу батьківського класу `super.method()`.
 */

// =========================================================================
// КЛАС 1: Фінансовий рахунок (Account) — сутність для агрегації
// =========================================================================
class Account {
    private String accountNumber;
    private double balance;

    // Конструктор 1: без параметрів (за замовчуванням)
    public Account() {
        this.accountNumber = "UA00000000";
        this.balance = 0.0;
    }

    // Конструктор 2: з параметрами
    public Account(String accountNumber, double balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    // Геттери та сеттери
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

    // Специфічний метод: показ інформації про об'єкт
    public void showInfo() {
        System.out.printf("   [Рахунок] Номер: %-12s | Баланс: %.2f грн\n", accountNumber, balance);
    }
}

// =========================================================================
// КЛАС 2: Базовий клас за варіантом 14 — "Фінансова установа" (FinancialInstitution)
// =========================================================================
class FinancialInstitution {
    // protected поля: доступні для класів-нащадків
    protected String name;
    protected String licenseCode;
    protected double baseCapital;

    // Конструктор 1: без параметрів
    public FinancialInstitution() {
        this.name = "Невідома фінансова установа";
        this.licenseCode = "LIC-00000";
        this.baseCapital = 1_000_000.0;
    }

    // Конструктор 2: з параметрами
    public FinancialInstitution(String name, String licenseCode, double baseCapital) {
        this.name = name;
        this.licenseCode = licenseCode;
        this.baseCapital = baseCapital;
    }

    // Геттери та сеттери
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

    // Специфічний метод 1: показ базової інформації
    public void showInfo() {
        System.out.println("==========================================================");
        System.out.println("Фінансова установа: " + name);
        System.out.println("Номер ліцензії:    " + licenseCode);
        System.out.printf("Статутний капітал: %.2f грн\n", baseCapital);
    }

    // Перевантажений метод (Overloading) 1: без параметрів
    public void describeActivity() {
        System.out.println("Діяльність: Надання загальних фінансових послуг населенню та бізнесу.");
    }

    // Перевантажений метод (Overloading) 2: з параметром деталізації
    public void describeActivity(String sector) {
        System.out.println("Діяльність: Спеціалізовані фінансові послуги у секторі: " + sector);
    }
}

// =========================================================================
// КЛАС 3: Клас-нащадок "Банк" (Bank) — успадковує FinancialInstitution
//        та АГРЕГУЄ масив рахунків Account[]
// =========================================================================
class Bank extends FinancialInstitution {
    private String swiftCode;
    // АГРЕГАЦІЯ: масив об'єктів рахунків Account
    private Account[] accounts;

    // Конструктор 1: без параметрів
    public Bank() {
        super(); // виклик конструктора базового класу FinancialInstitution
        this.name = "Національний Стандарт Банк";
        this.swiftCode = "NSTDUA2X";
        this.accounts = new Account[0]; // порожній список рахунків
    }

    // Конструктор 2: з параметрами
    public Bank(String name, String licenseCode, double baseCapital, String swiftCode, Account[] accounts) {
        super(name, licenseCode, baseCapital); // ініціалізація полів суперкласу через super
        this.swiftCode = swiftCode;
        
        // Копіювання масиву рахунків (агрегація)
        if (accounts != null) {
            this.accounts = new Account[accounts.length];
            for (int i = 0; i < accounts.length; i++) {
                this.accounts[i] = accounts[i];
            }
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

    // Специфічний розрахунковий метод: підрахунок сумарного балансу всіх агрегованих рахунків
    public double getTotalAccountsBalance() {
        double total = 0.0;
        for (Account acc : accounts) {
            total += acc.getBalance();
        }
        return total;
    }

    // ПЕРЕВИЗНАЧЕНИЙ метод (Overriding): розширює showInfo базового класу
    @Override
    public void showInfo() {
        super.showInfo(); // виклик методу батьківського класу через super
        System.out.println("SWIFT код банку:   " + swiftCode);
        System.out.println("Кількість рахунків: " + accounts.length);
        System.out.println("Список відкритих клієнтських рахунків:");
        if (accounts.length == 0) {
            System.out.println("   (Немає відкритих рахунків)");
        } else {
            for (Account acc : accounts) {
                acc.showInfo(); // делегування виклику агрегованому об'єкту
            }
        }
        System.out.printf("Загальний баланс клієнтських коштів: %.2f грн\n", getTotalAccountsBalance());
        System.out.println("==========================================================");
    }

    // ПЕРЕВИЗНАЧЕНИЙ метод (Overriding): власна реалізація опису діяльності банку
    @Override
    public void describeActivity() {
        System.out.println("Діяльність банку: Розрахунково-касове обслуговування, кредитування, депозити.");
    }

    // ПЕРЕВАНТАЖЕНИЙ метод (Overloading) у нащадку: додано додатковий параметр
    public void describeActivity(String sector, boolean isOnline) {
        String format = isOnline ? "дистанційно через Інтернет-банкінг" : "через мережу відділень";
        System.out.printf("Діяльність банку: Послуги у секторі '%s' надаються %s.\n", sector, format);
    }
}

// =========================================================================
// ГОЛОВНИЙ КЛАС ДЛЯ ДЕМОНСТРАЦІЇ РОБОТИ
// =========================================================================
public class Main {
    public static void main(String[] args) {
        System.out.println("ЛАБОРАТОРНА РОБОТА №6: Агрегація та успадкування");
        System.out.println("Варіант 14: Фінансова установа\n");

        // -------------------------------------------------------------
        // 1. Демонстрація класу 1: Account (Рахунок)
        // -------------------------------------------------------------
        System.out.println("--- 1. ДЕМОНСТРАЦІЯ КЛАСУ Account ---");
        Account accDefault = new Account(); // конструктор без параметрів
        System.out.print("accDefault (без параметрів): ");
        accDefault.showInfo();

        Account acc1 = new Account("UA26300123", 45000.50);
        Account acc2 = new Account("UA26300456", 120500.00);
        Account acc3 = new Account("UA26300789", 15200.25);
        System.out.println("Створено рахунки acc1, acc2, acc3 через конструктори з параметрами.");

        // -------------------------------------------------------------
        // 2. Демонстрація класу 2: FinancialInstitution (Базовий клас)
        // -------------------------------------------------------------
        System.out.println("\n--- 2. ДЕМОНСТРАЦІЯ БАЗОВОГО КЛАСУ FinancialInstitution ---");
        FinancialInstitution instDefault = new FinancialInstitution(); // конструктор без параметрів
        instDefault.showInfo();
        instDefault.describeActivity(); // базовий метод

        FinancialInstitution instCustom = new FinancialInstitution("ДержФінКонтроль", "LIC-778899", 5_000_000.0);
        instCustom.showInfo();
        // Демонстрація перевантаженого методу (Overloading)
        instCustom.describeActivity();
        instCustom.describeActivity("Мікрокредитування");

        // -------------------------------------------------------------
        // 3. Демонстрація класу 3: Bank (Нащадок з агрегацією Account[])
        // -------------------------------------------------------------
        System.out.println("\n--- 3. ДЕМОНСТРАЦІЯ КЛАСУ-НАЩАДКА Bank (Успадкування + Агрегація) ---");
        
        // 3.1. Банк через конструктор без параметрів
        System.out.println("\n>> Створення банку через конструктор БЕЗ параметрів:");
        Bank defaultBank = new Bank();
        defaultBank.showInfo();

        // 3.2. Банк через конструктор З параметрами (передаємо агрегований масив рахунків)
        System.out.println("\n>> Створення банку через конструктор З параметрами та агрегацією рахунків:");
        Account[] clientAccounts = {acc1, acc2, acc3};
        Bank agroBank = new Bank("АгроПромінвест Банк", "LIC-140514", 25_000_000.0, "AGROUA2X", clientAccounts);
        
        // Виклик перевизначеного (Overridden) методу showInfo()
        agroBank.showInfo();

        // -------------------------------------------------------------
        // 4. Демонстрація поліморфізму, перевизначення та перевантаження
        // -------------------------------------------------------------
        System.out.println("\n--- 4. ДЕМОНСТРАЦІЯ ПЕРЕВИЗНАЧЕННЯ ТА ПЕРЕВАНТАЖЕННЯ МЕТОДІВ ---");
        
        System.out.println("\n>> Перевизначений метод describeActivity() у Bank:");
        agroBank.describeActivity(); // перевизначена версія

        System.out.println("\n>> Перевантажений метод describeActivity(sector):");
        agroBank.describeActivity("Корпоративні клієнти");

        System.out.println("\n>> Перевантажений метод describeActivity(sector, isOnline) у Bank:");
        agroBank.describeActivity("Роздрібні клієнти", true);

        // -------------------------------------------------------------
        // 5. Демонстрація поліморфізму посилань (Dynamic Binding)
        // -------------------------------------------------------------
        System.out.println("\n--- 5. ДЕМОНСТРАЦІЯ ПОЛІМОРФІЗМУ ПОСИЛАНЬ ---");
        // Посилання типу FinancialInstitution вказує на об'єкт Bank
        FinancialInstitution polyRef = agroBank;
        System.out.println("Виклик polyRef.showInfo() (посилання FinancialInstitution, об'єкт Bank):");
        polyRef.showInfo(); // викличеться метод класу Bank завдяки динамічному зв'язуванню!
    }
}
