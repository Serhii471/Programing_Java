import java.util.Scanner;

/**
 * Лабораторна робота №1
 * Предмет: Програмування Java
 * Варіант 14
 *
 * Обчислення виразу:
 * R = (sin(x^2 + 4)^3 + 4.3) / (sin^3(x^4))
 *
 * Один аргумент (x) вводиться з клавіатури,
 * інші значення ініціалізовані конкретними значеннями.
 */
public class Main {

    public static void main(String[] args) {

        // Ініціалізація констант виразу
        double constant1 = 4.0;   // константа у аргументі sin: (x^2 + 4)
        double constant2 = 4.3;   // доданок у чисельнику
        int power = 3;            // степінь у чисельнику та знаменнику

        // Введення значення x з клавіатури
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введіть значення x: ");
        double x = scanner.nextDouble();
        scanner.close();

        // Обчислення чисельника: sin(x^2 + 4)^3 + 4.3
        double sinArg = x * x + constant1;          // x^2 + 4
        double numerator = Math.pow(Math.sin(sinArg), power) + constant2;

        // Обчислення знаменника: sin^3(x^4)
        double xPow4 = Math.pow(x, 4);              // x^4
        double sinX4 = Math.sin(xPow4);              // sin(x^4)
        double denominator = Math.pow(sinX4, power);  // sin^3(x^4)

        // Перевірка ділення на нуль
        if (Math.abs(denominator) < 1e-10) {
            System.out.println("Помилка: знаменник дорівнює нулю (sin^3(x^4) = 0).");
            System.out.println("Обчислення неможливе для x = " + x);
            return;
        }

        // Обчислення результату
        double R = numerator / denominator;

        // Виведення результату
        System.out.println("========================================");
        System.out.println("Варіант 14");
        System.out.println("R = (sin(x^2 + 4)^3 + 4.3) / sin^3(x^4)");
        System.out.println("========================================");
        System.out.println("x = " + x);
        System.out.println("Чисельник = " + numerator);
        System.out.println("Знаменник = " + denominator);
        System.out.println("R = " + R);
    }
}
