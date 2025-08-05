package com.example.demo.service;

import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook; // For .xls
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

public class ExcelTextExtractor {
    public static String extractTextFromExcel(MultipartFile file) {
        StringBuilder sb = new StringBuilder();
        try (Workbook workbook = getWorkbook(file)) {
            for (Sheet sheet : workbook) {
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        sb.append(cell.toString()).append("\t");
                    }
                    sb.append("\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to extract Excel content", e);
        }
        return sb.toString();
    }

    private static Workbook getWorkbook(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        if (filename != null && filename.endsWith(".xls")) {
            return new HSSFWorkbook(file.getInputStream()); // for .xls
        } else {
            return new XSSFWorkbook(file.getInputStream()); // for .xlsx
        }
    }
}

