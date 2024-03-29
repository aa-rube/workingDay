package app.factory.util;

import app.factory.model.WorkingDay;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.time.LocalDateTime;

import java.util.Iterator;
import java.util.Locale;

public class ExcelUpdater {
    private final static String PATH = "/home/onixmore/workingDay/data/workbook.xlsm";
    public static void writeData(WorkingDay day) throws IOException {
        try (FileInputStream fis = new FileInputStream(PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            updateOrInsertDate(workbook, day);

            try (FileOutputStream fileOut = new FileOutputStream(PATH)) {
                workbook.write(fileOut);
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private static void updateOrInsertDate(Workbook workbook, WorkingDay workingDay) {
        Sheet sheet = workbook.getSheet(workingDay.isExtraDay() ? "сверхур" : "Data_Tech");

        int columnIndexYear = 1;
        int columnIndexItem = 2;
        int columnIndexBatch = 3;
        int columnIndexCoefficient = 4;
        int columnIndexFullName = 5;
        int columnIndexLevel = 6;

        boolean foundMatch = false;

        Iterator<Row> rowIterator = sheet.iterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (isRowEmpty(row, columnIndexFullName)) {
                createRow(row, workingDay);
                foundMatch = true;
                break;
            }

            Cell cellYear = row.getCell(columnIndexYear);
            Cell cellItem = row.getCell(columnIndexItem);
            Cell cellBatch = row.getCell(columnIndexBatch);
            Cell cellCoefficient = row.getCell(columnIndexCoefficient);
            Cell cellFullName = row.getCell(columnIndexFullName);
            Cell cellLevel = row.getCell(columnIndexLevel);

            if (cellYear != null && cellItem != null &&
                    cellBatch != null && cellCoefficient != null &&
                    cellFullName != null && cellLevel != null) {

                String year = getCellValueAsString(cellYear);
                String itemName = getCellValueAsString(cellItem);
                String batch = getCellValueAsString(cellBatch);
                String coefficient = getCellValueAsString(cellCoefficient);
                String fullName = getCellValueAsString(cellFullName);
                String level = getCellValueAsString(cellLevel);

                if (year.equals(String.valueOf(workingDay.getLocalDateTime().getYear())) &&
                        itemName.equals(workingDay.getItem()) &&
                        batch.equals(workingDay.getBatch()) &&
                        coefficient.equals(workingDay.getCoefficient()) &&
                        fullName.equals(workingDay.getFullName()) &&
                        level.equals(workingDay.getLevel())) {

                    int dateRow = 6 + workingDay.getLocalDateTime().getDayOfMonth();
                    row.createCell(dateRow).setCellValue(workingDay.getWorkingTime());

                    foundMatch = true;
                    break;
                }
            }
        }

        if (!foundMatch) {
            Row newRow = sheet.createRow(sheet.getLastRowNum() + 1);
            createRow(newRow, workingDay);
        }
    }

    private static boolean isRowEmpty(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        return cell == null || cell.getCellType() == CellType.BLANK;
    }


    private static void createRow(Row row, WorkingDay workingDay) {
        row.createCell(1).setCellValue(workingDay.getLocalDateTime().getYear());
        row.createCell(2).setCellValue(workingDay.getItem());
        row.createCell(3).setCellValue(workingDay.getBatch());
        row.createCell(4).setCellValue(workingDay.getCoefficient());
        row.createCell(5).setCellValue(workingDay.getFullName());
        row.createCell(6).setCellValue(workingDay.getLevel());

        int dateRow = 6 + workingDay.getLocalDateTime().getDayOfMonth();
        Cell dateCell = row.createCell(dateRow);
        dateCell.setCellValue(workingDay.getWorkingTime());

        applyStyles(row, dateRow);
    }

    private static void applyStyles(Row row, int dateRow) {
        Workbook workbook = row.getSheet().getWorkbook();
        CellStyle style = workbook.createCellStyle();

        for (int i = 0; i <= dateRow; i++) {
            Cell cell = row.getCell(i);
            if (cell != null) {
                style.setBorderTop(BorderStyle.THIN);
                style.setBorderBottom(BorderStyle.THIN);
                style.setBorderLeft(BorderStyle.THIN);
                style.setBorderRight(BorderStyle.THIN);
                cell.setCellStyle(style);
            }
        }
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((int) cell.getNumericCellValue());
        } else {
            return cell.getStringCellValue();
        }
    }
}
