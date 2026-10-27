package ua.edu.nubip.module2.io;

import ua.edu.nubip.module2.model.FinancialInstitution;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Реалізує текстове та двійкове збереження об'єктів із ЛР10.
 */
public final class InstitutionFileService {
    /** Мінімальна кількість записів відповідно до ЛР8. */
    private static final int MINIMUM_COUNT = 10;
    /** Захисне обмеження кількості записів двійкового файла. */
    private static final int MAXIMUM_SAFE_COUNT = 10_000;

    /** Забороняє створення службового класу. */
    private InstitutionFileService() {
    }

    /**
     * Зчитує UTF-8 текстовий файл формату
     * {@code назва;код ліцензії;капітал}.
     *
     * @param file шлях до текстового файла
     * @return масив відновлених установ
     * @throws IOException якщо файл пошкоджений або недоступний
     */
    public static FinancialInstitution[] readText(Path file) throws IOException {
        List<FinancialInstitution> result = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                String[] fields = trimmed.split(";", -1);
                if (fields.length != 3) {
                    throw new IOException("Рядок " + lineNumber + " повинен містити 3 поля.");
                }
                try {
                    double capital = Double.parseDouble(fields[2].trim().replace(',', '.'));
                    result.add(new FinancialInstitution(fields[0], fields[1], capital));
                } catch (IllegalArgumentException e) {
                    throw new IOException("Некоректні дані в рядку " + lineNumber + ": "
                            + e.getMessage(), e);
                }
            }
        }
        validateCount(result.size(), "текстовому");
        return result.toArray(new FinancialInstitution[0]);
    }

    /**
     * Записує масив у типізований двійковий файл.
     *
     * @param file шлях до файла призначення
     * @param institutions масив для запису
     * @throws IOException якщо запис не вдався
     */
    public static void writeBinary(
            Path file,
            FinancialInstitution[] institutions
    ) throws IOException {
        if (institutions == null || institutions.length < MINIMUM_COUNT) {
            throw new IllegalArgumentException("Для запису потрібно щонайменше 10 установ.");
        }
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (DataOutputStream output = new DataOutputStream(
                new BufferedOutputStream(Files.newOutputStream(file)))) {
            output.writeInt(institutions.length);
            for (FinancialInstitution institution : institutions) {
                output.writeUTF(institution.getName());
                output.writeUTF(institution.getLicenseCode());
                output.writeDouble(institution.getBaseCapital());
            }
        }
    }

    /**
     * Відновлює масив із типізованого двійкового файла.
     *
     * @param file шлях до файла
     * @return масив відновлених установ
     * @throws IOException якщо файл пошкоджений або недоступний
     */
    public static FinancialInstitution[] readBinary(Path file) throws IOException {
        try (DataInputStream input = new DataInputStream(
                new BufferedInputStream(Files.newInputStream(file)))) {
            int count = input.readInt();
            if (count < MINIMUM_COUNT || count > MAXIMUM_SAFE_COUNT) {
                throw new IOException("Некоректна кількість записів: " + count);
            }
            FinancialInstitution[] result = new FinancialInstitution[count];
            for (int i = 0; i < count; i++) {
                result[i] = new FinancialInstitution(
                        input.readUTF(),
                        input.readUTF(),
                        input.readDouble()
                );
            }
            return result;
        } catch (EOFException e) {
            throw new IOException("Двійковий файл записаний не повністю.", e);
        } catch (IllegalArgumentException e) {
            throw new IOException("Двійковий файл містить некоректні дані.", e);
        }
    }

    /**
     * Перевіряє мінімальну кількість записів.
     *
     * @param count фактична кількість
     * @param fileType назва типу файла для повідомлення
     * @throws IOException якщо записів замало
     */
    private static void validateCount(int count, String fileType) throws IOException {
        if (count < MINIMUM_COUNT) {
            throw new IOException("У " + fileType + " файлі має бути щонайменше 10 записів.");
        }
    }
}
