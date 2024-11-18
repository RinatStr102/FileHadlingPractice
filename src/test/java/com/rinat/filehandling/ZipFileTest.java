package com.rinat.filehandling;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ZipFileTest {
    @Test
    void readZipFileTest() throws Exception {
        // Путь к архиву
        String zipFilePath = "src/test/resources/files.zip";
        //Открыываем zip-архив
        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            //Переберем содержимое архив
            zipFile.entries().asIterator().forEachRemaining(entry -> {
                System.out.println("Файл в архиве: " + entry.getName());
            });
        }
    }

    @Test
    void checkCsvFileContent() throws Exception {
        String zipFilePath = "src/test/resources/files.zip";
        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            ZipEntry csvEntry = zipFile.getEntry("example.csv");
            assertNotNull(csvEntry, "CSV-файл отсутствует в архиве!");
            try (InputStream inputStream = zipFile.getInputStream(csvEntry);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                var lines = reader.lines().toList();

                // Проверяем содержимое файла
                assertTrue(lines.contains("Name,Age,City"), "Заголовок 'Name,Age,City' отсутствует!");
                assertTrue(lines.contains("Mansur,25,Sterlitamak"), "Строка 'Mansur,25,Sterlitamak' отсутствует!");
                assertTrue(lines.contains("Flur,30.Salavat"), "Строка 'Flur,30.Salavat' отсутствует!");
                assertTrue(lines.contains("Bob,23,New York"), "Строка 'Bob,23,New York' отсутствует!");
            }
        }

    }
    @Test
    void checkXlsxFileContent() throws Exception {
        // Путь к ZIP-архиву
        String zipFilePath = "src/test/resources/files.zip";

        // Открываем архив
        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            // Проверяем наличие файла example.xlsx в архиве
            ZipEntry xlsxEntry = zipFile.getEntry("example.xlsx");
            assertNotNull(xlsxEntry, "XLSX-файл отсутствует в архиве!");

            // Читаем содержимое файла
            try (InputStream inputStream = zipFile.getInputStream(xlsxEntry)) {
                Workbook workbook = WorkbookFactory.create(inputStream);
                Sheet sheet = workbook.getSheetAt(0); // Берем первый лист

                // Проверяем заголовки (первая строка)
                Row headerRow = sheet.getRow(0); // Строка 1
                assertNotNull(headerRow, "Заголовки отсутствуют!");
                assertEquals("Name", headerRow.getCell(0).getStringCellValue(), "Ошибка в заголовке колонки A!");
                assertEquals("Age", headerRow.getCell(1).getStringCellValue(), "Ошибка в заголовке колонки B!");
                assertEquals("City", headerRow.getCell(2).getStringCellValue(), "Ошибка в заголовке колонки C!");

                // Проверяем данные
                Row firstDataRow = sheet.getRow(1); // Строка 2
                assertNotNull(firstDataRow, "Данные в строке 2 отсутствуют!");
                assertEquals("John", firstDataRow.getCell(0).getStringCellValue(), "Ошибка в данных строки 2, колонка A!");
                assertEquals(25, (int) firstDataRow.getCell(1).getNumericCellValue(), "Ошибка в данных строки 2, колонка B!");
                assertEquals("New York", firstDataRow.getCell(2).getStringCellValue(), "Ошибка в данных строки 2, колонка C!");

                Row secondDataRow = sheet.getRow(2); // Строка 3
                assertNotNull(secondDataRow, "Данные в строке 3 отсутствуют!");
                assertEquals("Alica", secondDataRow.getCell(0).getStringCellValue(), "Ошибка в данных строки 3, колонка A!");
                assertEquals(30, (int) secondDataRow.getCell(1).getNumericCellValue(), "Ошибка в данных строки 3, колонка B!");
                assertEquals("Los Angeles", secondDataRow.getCell(2).getStringCellValue(), "Ошибка в данных строки 3, колонка C!");

                Row thirdDataRow = sheet.getRow(3); // Строка 4
                assertNotNull(thirdDataRow, "Данные в строке 4 отсутствуют!");
                assertEquals("Bob", thirdDataRow.getCell(0).getStringCellValue(), "Ошибка в данных строки 4, колонка A!");
                assertEquals(22, (int) thirdDataRow.getCell(1).getNumericCellValue(), "Ошибка в данных строки 4, колонка B!");
                assertEquals("Chicago", thirdDataRow.getCell(2).getStringCellValue(), "Ошибка в данных строки 4, колонка C!");
            }
        }
    }
    @Test
    void checkPdfFileContent() throws Exception {
        // Указываем путь к ZIP-архиву
        String zipFilePath = "src/test/resources/files.zip";

        // Открываем ZIP-архив
        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            // Ищем файл с именем "example.pdf" в архиве
            ZipEntry pdfEntry = zipFile.getEntry("example.pdf");
            assertNotNull(pdfEntry, "PDF-файл отсутствует в архиве!");

            // Если файл найден, читаем его содержимое
            try (InputStream inputStream = zipFile.getInputStream(pdfEntry)) {
                // Загружаем PDF-документ из потока
                PDDocument document = PDDocument.load(inputStream);

                // Извлекаем текст из PDF-документа
                PDFTextStripper pdfStripper = new PDFTextStripper();
                String text = pdfStripper.getText(document);

                // Закрываем документ
                document.close();

                // Проверяем, что текст "Это тестовый PDF-файл" присутствует
                assertTrue(text.contains("Это тестовый PDF-файл"), "Содержимое PDF-файла некорректно!");
            }
        }
    }

}

