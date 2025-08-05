package com.example.demo.service;

import java.io.IOException;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.web.multipart.MultipartFile;

public class DocxTextExtractor {
    public static String extractTextFromDoc(MultipartFile file) {
        StringBuilder sb = new StringBuilder();
        try (XWPFDocument document = new XWPFDocument(file.getInputStream())) {
            for (XWPFParagraph para : document.getParagraphs()) {
                sb.append(para.getText()).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to extract text from DOCX file", e);
        }
        return sb.toString();
    }
}
