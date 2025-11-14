package com.example.pariba.services.impl;

import com.example.pariba.models.Person;
import com.example.pariba.models.TontineGroup;
import com.example.pariba.models.Payment;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service pour générer les fichiers d'export (PDF, Excel)
 */
@Service
@Slf4j
public class FileGeneratorService {
    
    @Value("${app.export.directory:./exports}")
    private String exportDirectory;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    /**
     * Génère un fichier PDF pour une liste d'utilisateurs
     */
    public String generateUsersPdf(List<Person> users, String jobId) throws Exception {
        log.info(" Génération PDF pour {} utilisateurs", users.size());
        
        String fileName = String.format("users_%s_%s.pdf", jobId, System.currentTimeMillis());
        Path filePath = ensureExportDirectory().resolve(fileName);
        
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, new FileOutputStream(filePath.toFile()));
        document.open();
        
        try {
            // Titre
            com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
            Paragraph title = new Paragraph("Liste des Utilisateurs - Pariba", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            
            // Date
            com.itextpdf.text.Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
            Paragraph date = new Paragraph("Généré le: " + LocalDateTime.now().format(DATE_FORMATTER), dateFont);
            date.setAlignment(Element.ALIGN_RIGHT);
            date.setSpacingAfter(20);
            document.add(date);
            
            // Table
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3, 3, 2, 2, 2});
            
            // Header
            com.itextpdf.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
            addTableHeader(table, headerFont, "Nom Complet", "Email", "Téléphone", "Rôle", "Date Création");
            
            // Data
            com.itextpdf.text.Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            for (Person user : users) {
                addTableCell(table, dataFont, user.getPrenom() + " " + user.getNom());
                addTableCell(table, dataFont, user.getEmail());
                addTableCell(table, dataFont, user.getPhone());
                addTableCell(table, dataFont, user.getRole().name());
                addTableCell(table, dataFont, user.getCreatedAt().toString().substring(0, 10));
            }
            
            document.add(table);
            
            // Footer
            Paragraph footer = new Paragraph("\nTotal: " + users.size() + " utilisateurs", dateFont);
            footer.setAlignment(Element.ALIGN_RIGHT);
            document.add(footer);
            
        } finally {
            document.close();
        }
        
        return fileName;
    }
    
    /**
     * Génère un fichier Excel pour une liste d'utilisateurs
     */
    public String generateUsersExcel(List<Person> users, String jobId) throws Exception {
        log.info("Génération Excel pour {} utilisateurs", users.size());
        
        String fileName = String.format("users_%s_%s.xlsx", jobId, System.currentTimeMillis());
        Path filePath = ensureExportDirectory().resolve(fileName);
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Utilisateurs");
        
        // Style header
        CellStyle headerStyle = createHeaderStyle(workbook, IndexedColors.DARK_BLUE);
        
        // Header
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Nom Complet", "Email", "Téléphone", "Rôle", "Date Création"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Data
        int rowNum = 1;
        for (Person user : users) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(user.getPrenom() + " " + user.getNom());
            row.createCell(1).setCellValue(user.getEmail());
            row.createCell(2).setCellValue(user.getPhone());
            row.createCell(3).setCellValue(user.getRole().name());
            row.createCell(4).setCellValue(user.getCreatedAt().toString().substring(0, 10));
        }
        
        // Auto-size
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        try (FileOutputStream fileOut = new FileOutputStream(filePath.toFile())) {
            workbook.write(fileOut);
        }
        workbook.close();
        
        return fileName;
    }
    
    /**
     * Génère un fichier PDF pour une liste de groupes
     */
    public String generateGroupsPdf(List<TontineGroup> groups, String jobId) throws Exception {
        log.info(" Génération PDF pour {} groupes", groups.size());
        
        String fileName = String.format("groups_%s_%s.pdf", jobId, System.currentTimeMillis());
        Path filePath = ensureExportDirectory().resolve(fileName);
        
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, new FileOutputStream(filePath.toFile()));
        document.open();
        
        try {
            com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
            Paragraph title = new Paragraph("Liste des Groupes - Pariba", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            
            com.itextpdf.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
            addTableHeader(table, headerFont, "Nom", "Créateur", "Membres", "Montant", "Statut");
            
            com.itextpdf.text.Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            for (TontineGroup group : groups) {
                addTableCell(table, dataFont, group.getNom());
                addTableCell(table, dataFont, group.getCreator().getPrenom() + " " + group.getCreator().getNom());
                addTableCell(table, dataFont, String.valueOf(group.getTotalTours()));
                addTableCell(table, dataFont, group.getMontant() + " FCFA");
                addTableCell(table, dataFont, "ACTIF"); // Pas de champ active dans le modèle
            }
            
            document.add(table);
            
        } finally {
            document.close();
        }
        
        return fileName;
    }
    
    /**
     * Génère un fichier Excel pour une liste de groupes
     */
    public String generateGroupsExcel(List<TontineGroup> groups, String jobId) throws Exception {
        log.info(" Génération Excel pour {} groupes", groups.size());
        
        String fileName = String.format("groups_%s_%s.xlsx", jobId, System.currentTimeMillis());
        Path filePath = ensureExportDirectory().resolve(fileName);
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Groupes");
        
        CellStyle headerStyle = createHeaderStyle(workbook, IndexedColors.DARK_GREEN);
        
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Nom", "Créateur", "Membres", "Montant", "Fréquence", "Statut"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        int rowNum = 1;
        for (TontineGroup group : groups) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(group.getNom());
            row.createCell(1).setCellValue(group.getCreator().getPrenom() + " " + group.getCreator().getNom());
            row.createCell(2).setCellValue(group.getTotalTours());
            row.createCell(3).setCellValue(group.getMontant() + " FCFA");
            row.createCell(4).setCellValue(group.getFrequency().name());
            row.createCell(5).setCellValue("ACTIF");
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        try (FileOutputStream fileOut = new FileOutputStream(filePath.toFile())) {
            workbook.write(fileOut);
        }
        workbook.close();
        
        return fileName;
    }
    
    /**
     * Génère un fichier PDF pour une liste de paiements
     */
    public String generatePaymentsPdf(List<Payment> payments, String jobId) throws Exception {
        log.info(" Génération PDF pour {} paiements", payments.size());
        
        String fileName = String.format("payments_%s_%s.pdf", jobId, System.currentTimeMillis());
        Path filePath = ensureExportDirectory().resolve(fileName);
        
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, new FileOutputStream(filePath.toFile()));
        document.open();
        
        try {
            com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
            Paragraph title = new Paragraph("Liste des Paiements - Pariba", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            
            com.itextpdf.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
            addTableHeader(table, headerFont, "ID", "Montant", "Statut", "Méthode", "Date");
            
            com.itextpdf.text.Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            for (Payment payment : payments) {
                addTableCell(table, dataFont, payment.getId().substring(0, 8));
                addTableCell(table, dataFont, payment.getAmount() + " FCFA");
                addTableCell(table, dataFont, payment.getStatus().name());
                addTableCell(table, dataFont, payment.getPaymentType() != null ? payment.getPaymentType().name() : "N/A");
                addTableCell(table, dataFont, payment.getCreatedAt().toString().substring(0, 16));
            }
            
            document.add(table);
            
        } finally {
            document.close();
        }
        
        return fileName;
    }
    
    /**
     * Génère un fichier Excel pour une liste de paiements
     */
    public String generatePaymentsExcel(List<Payment> payments, String jobId) throws Exception {
        log.info(" Génération Excel pour {} paiements", payments.size());
        
        String fileName = String.format("payments_%s_%s.xlsx", jobId, System.currentTimeMillis());
        Path filePath = ensureExportDirectory().resolve(fileName);
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Paiements");
        
        CellStyle headerStyle = createHeaderStyle(workbook, IndexedColors.ORANGE);
        
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Montant", "Statut", "Méthode", "Date"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        int rowNum = 1;
        for (Payment payment : payments) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(payment.getId().substring(0, 8));
            row.createCell(1).setCellValue(payment.getAmount() + " FCFA");
            row.createCell(2).setCellValue(payment.getStatus().name());
            row.createCell(3).setCellValue(payment.getPaymentType() != null ? payment.getPaymentType().name() : "N/A");
            row.createCell(4).setCellValue(payment.getCreatedAt().toString().substring(0, 16));
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        try (FileOutputStream fileOut = new FileOutputStream(filePath.toFile())) {
            workbook.write(fileOut);
        }
        workbook.close();
        
        return fileName;
    }
    
    // Helper methods
    private Path ensureExportDirectory() throws Exception {
        Path path = Paths.get(exportDirectory);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        return path;
    }
    
    private void addTableHeader(PdfPTable table, com.itextpdf.text.Font font, String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, font));
            cell.setBackgroundColor(BaseColor.DARK_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8);
            table.addCell(cell);
        }
    }
    
    private void addTableCell(PdfPTable table, com.itextpdf.text.Font font, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        table.addCell(cell);
    }
    
    private CellStyle createHeaderStyle(Workbook workbook, IndexedColors color) {
        CellStyle style = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(color.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
}
