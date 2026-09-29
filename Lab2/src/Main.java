import java.util.Scanner;

/**
 * Лабораторна робота №2
 * Предмет: Програмування Java
 * Варіант 14
 *
 * Практична частина:
 * Додано перевірку області допустимих значень (ОДЗ).
 * Вираз: R = (sin(x^2 + 4)^3 + 4.3) / (sin^3(x^4))
 * ОДЗ: знаменник не дорівнює нулю -> sin(x^4) != 0
 */
public class Main {

    public static void main(String[] args) {

        double constant1 = 4.0;   // константа у аргументі sin: (x^2 + 4)
        double constant2 = 4.3;   // доданок у чисельнику
        int power = 3;            // степінь у чисельнику та знаменнику

        Scanner scanner = new Scanner(System.in);
        System.out.print("Введіть значення x: ");
        
        if (!scanner.hasNextDouble()) {
            System.out.println("Помилка: Введено некоректне число.");
            scanner.close();
            return;
        }
        
        double x = scanner.nextDouble();
        scanner.close();

        // Обчислення проміжних значень для знаменника (для перевірки ОДЗ)
        double xPow4 = Math.pow(x, 4);
        double sinX4 = Math.sin(xPow4);
        double denominator = Math.pow(sinX4, power);

        // Перевірка Області Допустимих Значень (ОДЗ)
        // Знаменник не може дорівнювати нулю. Оскільки ми працюємо з числами
        // з плаваючою комою, порівнюємо з дуже малим числом (epsilon)
        if (Math.abs(denominator) < 1e-10) {
            System.out.println("\n--- ПОМИЛКА ОБЧИСЛЕННЯ ---");
            System.out.println("Значення x = " + x + " не належить області допустимих значень (ОДЗ).");
            System.out.println("Причина: знаменник sin^3(x^4) дорівнює нулю (виникає ділення на нуль).");
            System.out.println("--------------------------");
            return;
        }

        // Обчислення чисельника, оскільки ОДЗ пройдено успішно
        double sinArg = x * x + constant1;
        double numerator = Math.pow(Math.sin(sinArg), power) + constant2;

        // Обчислення результату
        double R = numerator / denominator;

        // Виведення результату
        System.out.println("\n========================================");
        System.out.println("Варіант 14 (Лабораторна робота 2)");
        System.out.println("R = (sin(x^2 + 4)^3 + 4.3) / sin^3(x^4)");
        System.out.println("========================================");
        System.out.println("Введено x = " + x);
        System.out.println("Чисельник = " + numerator);
        System.out.println("Знаменник = " + denominator);
        System.out.println("Результат R = " + R);
    }
}
