package mainapp;

import calc.ExpressionCalculator;
import java.util.Scanner;
import java.util.InputMismatchException;

/**
 * Головний клас програми (розміщений у пакеті mainapp).
 * Демонструє:
 * 1) Роботу двох конструкторів класу ExpressionCalculator (за замовчуванням та з параметрами).
 * 2) Роботу гетерів та сетерів.
 * 3) Виклик нестатичного та статичного методів обчислення виразу.
 * 4) Обчислення в циклі з перевіркою ОДЗ та виняткових ситуацій введення даних (як у попередній роботі).
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int N = 14;

        System.out.println("===============================================================");
        System.out.println("Лабораторна робота: Класи, об'єкти та методи в Java (Варіант 14)");
        System.out.println("Вираз: R = (sin(x^2 + 4)^3 + 4.3) / sin^3(x^4)");
        System.out.println("===============================================================");

        // 1. Демонстрація конструктора за замовчуванням
        System.out.println("\n[1] Демонстрація роботи конструктора за замовчуванням:");
        ExpressionCalculator defaultObj = new ExpressionCalculator();
        System.out.println("Створено об'єкт defaultObj зі значеннями за замовчуванням:");
        System.out.printf("x = %.2f, const1 = %.2f, const2 = %.2f, power = %d\n",
                defaultObj.getX(), defaultObj.getConstant1(), defaultObj.getConstant2(), defaultObj.getPower());

        double resultDefault = defaultObj.calculate();
        if (Double.isNaN(resultDefault)) {
            System.out.println("Результат обчислення: Значення не входить в ОДЗ!");
        } else {
            System.out.printf("Результат нестатичного методу defaultObj.calculate() = %.6f\n", resultDefault);
        }

        // 2. Демонстрація роботи сетерів
        System.out.println("\n[2] Демонстрація роботи сетерів (змінюємо x на 1.5):");
        defaultObj.setX(1.5);
        System.out.printf("Нове значення x = %.2f (отримано через getX())\n", defaultObj.getX());
        System.out.printf("Новий результат calculate() = %.6f\n", defaultObj.calculate());

        // 3. Зчитування номеру варіанту користувача для циклічного розрахунку з обробкою винятків
        System.out.println("\n[3] Введення варіанту N для розрахунку діапазону:");
        System.out.print("Введіть номер варіанту N (ціле число): ");

        try {
            N = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("\n--- Помилка некоректного введення ---");
            System.out.println("Виникла виняткова ситуація: " + e.toString());
            System.out.println("Помилка: потрібно ввести ціле число. Завершення програми.");
            System.out.println("-------------------------------------");
            return;
        } catch (Exception e) {
            System.out.println("\nНевідома помилка: " + e.getMessage());
            return;
        } finally {
            scanner.close();
            System.out.println("[finally] Потік введення закрито.");
        }

        // Розрахунок параметрів діапазону
        double startX = -10.0 - 2.5 * N;
        double endX = 5.0 + 1.2 * N;
        double step = 0.5 + (double) N / 20.0;

        System.out.println("\nДіапазон X: [" + startX + "; " + endX + "], Крок: " + step);

        // 4. Демонстрація конструктора з параметрами (створення другого об'єкта)
        System.out.println("\n[4] Демонстрація конструктора з параметрами та циклу (нестатичний метод):");
        ExpressionCalculator paramObj = new ExpressionCalculator(startX, 4.0, 4.3, 3);
        System.out.println("Створено об'єкт paramObj через конструктор з параметрами.");
        System.out.println("---------------------------------------------------------------");
        System.out.printf("%-15s | %-30s\n", "Значення x", "R (нестатичний метод)");
        System.out.println("---------------------------------------------------------------");

        for (double x = startX; x <= endX + 1e-9; x += step) {
            paramObj.setX(x); // Встановлюємо x в об'єкт через сетер
            double r = paramObj.calculate(); // Виклик нестатичного методу об'єкта

            if (Double.isNaN(r)) {
                System.out.printf("%-15.4f | %-30s\n", x, "Пропуск (ОДЗ: знаменник = 0)");
                continue;
            }
            System.out.printf("%-15.4f | %-30.6f\n", x, r);
        }

        // 5. Демонстрація виклику СТАТИЧНОГО методу класу
        System.out.println("\n[5] Демонстрація виклику СТАТИЧНОГО методу ExpressionCalculator.calculateStatic():");
        System.out.println("Статичний метод викликається безпосередньо через клас без створення нового об'єкта.");
        System.out.println("---------------------------------------------------------------");
        System.out.printf("%-15s | %-30s\n", "Значення x", "R (статичний метод)");
        System.out.println("---------------------------------------------------------------");

        double xWhile = startX;
        while (xWhile <= endX + 1e-9) {
            // Виклик статичного методу:
            double rStatic = ExpressionCalculator.calculateStatic(xWhile, 4.0, 4.3, 3);

            if (Double.isNaN(rStatic)) {
                System.out.printf("%-15.4f | %-30s\n", xWhile, "Пропуск (ОДЗ: знаменник = 0)");
                xWhile += step;
                continue;
            }

            System.out.printf("%-15.4f | %-30.6f\n", xWhile, rStatic);
            xWhile += step;
        }

        System.out.println("===============================================================");
        System.out.println("Роботу програми успішно завершено.");
    }
}
