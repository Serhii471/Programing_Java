import java.util.Scanner;
import java.util.InputMismatchException;

/**
 * Лабораторна робота №4
 * Тема: Циклічні конструкції
 * Варіант: 14
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int N = 14;

        System.out.println("Лабораторна робота 4 (Варіант 14)");
        System.out.println("Функція: R = (sin(x^2 + 4)^3 + 4.3) / sin^3(x^4)");
        System.out.print("Введіть ваш варіант N (ціле число): ");

        try {
            N = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("\n--- Помилка некоректного введення ---");
            System.out.println("Виникла виняткова ситуація: " + e.toString());
            System.out.println("Помилка: потрібно було ввести ціле число (номер варіанту).");
            System.out.println("-------------------------------------");
            return;
        } catch (Exception e) {
            System.out.println("\nНевідома помилка: " + e.getMessage());
            return;
        } finally {
            scanner.close();
            System.out.println("[finally] Робота зі сканером (введенням) завершена.");
        }

        // Обчислення діапазону та кроку згідно із завданням
        double startX = -10.0 - 2.5 * N;
        double endX = 5.0 + 1.2 * N;
        double step = 0.5 + (double) N / 20.0;

        System.out.println("\nДіапазон X: [" + startX + "; " + endX + "], Крок: " + step);
        System.out.println("===============================================================");

        // --- Спосіб 1: цикл FOR ---
        System.out.println("\n1. Результати за допомогою циклу FOR:");
        System.out.println("---------------------------------------------------------------");
        System.out.printf("%-15s | %-25s\n", "Значення x", "Значення R");
        System.out.println("---------------------------------------------------------------");
        
        for (double x = startX; x <= endX + 1e-9; x += step) {
            double denominator = Math.pow(Math.sin(Math.pow(x, 4)), 3);
            
            // Перевірка області допустимих значень (ОДЗ)
            // Якщо знаменник дорівнює нулю (з урахуванням похибки)
            if (Math.abs(denominator) < 1e-10) {
                System.out.printf("%-15.4f | %-25s\n", x, "Пропуск (ОДЗ, знаменник = 0)");
                continue; // Перехід на наступну ітерацію циклу
            }
            
            double numerator = Math.pow(Math.sin(x * x + 4.0), 3) + 4.3;
            double R = numerator / denominator;
            System.out.printf("%-15.4f | %-25.4f\n", x, R);
        }

        // --- Спосіб 2: цикл WHILE ---
        System.out.println("\n2. Результати за допомогою циклу WHILE:");
        System.out.println("---------------------------------------------------------------");
        System.out.printf("%-15s | %-25s\n", "Значення x", "Значення R");
        System.out.println("---------------------------------------------------------------");
        
        double xWhile = startX;
        while (xWhile <= endX + 1e-9) {
            double denominator = Math.pow(Math.sin(Math.pow(xWhile, 4)), 3);
            
            // Перевірка області допустимих значень (ОДЗ)
            if (Math.abs(denominator) < 1e-10) {
                System.out.printf("%-15.4f | %-25s\n", xWhile, "Пропуск (ОДЗ, знаменник = 0)");
                xWhile += step; // Обов'язково збільшуємо лічильник перед continue, щоб уникнути нескінченного циклу
                continue; 
            }
            
            double numerator = Math.pow(Math.sin(xWhile * xWhile + 4.0), 3) + 4.3;
            double R = numerator / denominator;
            System.out.printf("%-15.4f | %-25.4f\n", xWhile, R);
            
            xWhile += step;
        }
        System.out.println("===============================================================");
    }
}
