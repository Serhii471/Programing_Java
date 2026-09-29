import java.util.Scanner;
import java.util.InputMismatchException;

/**
 * Лабораторна робота №3
 * Предмет: Програмування Java
 * Варіант 14
 *
 * Практична частина:
 * Додано обробку виняткових ситуацій (try-catch-finally)
 * для перевірки некоректного введення користувача.
 */
public class Main {

    public static void main(String[] args) {

        double constant1 = 4.0;
        double constant2 = 4.3;
        int power = 3;
        double x = 0;

        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Варіант 14 (Лабораторна робота 3)");
        System.out.println("Обчислення: R = (sin(x^2 + 4)^3 + 4.3) / sin^3(x^4)");
        System.out.print("Введіть значення x (число): ");

        try {
            // Спроба зчитати число типу double
            x = scanner.nextDouble();
            
        } catch (InputMismatchException e) {
            // Перехоплення винятку, якщо користувач ввів текст або інші недопустимі символи
            System.out.println("\n--- ВИНИКЛА ВИНЯТКОВА СИТУАЦІЯ ---");
            System.out.println("Помилка (Exception): " + e.toString());
            System.out.println("Причина: Введено нечислові дані. Очікується десяткове число.");
            System.out.println("----------------------------------");
            return; // Завершуємо програму через помилку
            
        } catch (Exception e) {
            // Перехоплення інших непередбачуваних винятків (демонстрація множинних catch)
            System.out.println("Невідома помилка: " + e.getMessage());
            return;
            
        } finally {
            // Блок finally виконається гарантовано (незалежно від того, була помилка чи ні)
            System.out.println("[finally] Звільнення ресурсів: закриття Scanner.");
            scanner.close();
        }

        // --- Перевірка ОДЗ (як у ЛР2) ---
        double xPow4 = Math.pow(x, 4);
        double sinX4 = Math.sin(xPow4);
        double denominator = Math.pow(sinX4, power);

        if (Math.abs(denominator) < 1e-10) {
            System.out.println("\n--- ПОМИЛКА ОБЧИСЛЕННЯ ---");
            System.out.println("Значення x = " + x + " не належить області допустимих значень (ОДЗ).");
            System.out.println("Причина: знаменник дорівнює нулю (виникає ділення на нуль).");
            System.out.println("--------------------------");
            return;
        }

        // Обчислення чисельника і загального результату
        double sinArg = x * x + constant1;
        double numerator = Math.pow(Math.sin(sinArg), power) + constant2;
        double R = numerator / denominator;

        // Виведення результату
        System.out.println("\n========================================");
        System.out.println("Введено x = " + x);
        System.out.println("Чисельник = " + numerator);
        System.out.println("Знаменник = " + denominator);
        System.out.println("Результат R = " + R);
        System.out.println("========================================");
    }
}
