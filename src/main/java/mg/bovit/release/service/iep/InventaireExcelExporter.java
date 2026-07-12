package mg.bovit.release.service.iep;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import mg.bovit.release.model.Inventaire;
import mg.bovit.release.model.InventaireDetail;

public class InventaireExcelExporter {

    private static final String[] HEADERS = {
            "ID", "Date d'inventaire", "Libellé"
    };

    public static byte[] export(List<Inventaire> inventaires) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Inventaires");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            int rowIdx = 1;
            for (Inventaire inv : inventaires) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(inv.getId() != null ? inv.getId() : 0L);
                row.createCell(1).setCellValue(
                        inv.getDateInventaire() != null ? sdf.format(inv.getDateInventaire()) : "");
                row.createCell(2).setCellValue(
                        inv.getLibelle() != null ? inv.getLibelle() : "");
            }

            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private static final String[] DETAIL_HEADERS = {
            "Matériel", "Type matériel", "Qté Initiale", "Qté Réelle", "Qté Actuelle", "Écart", "Observations"
    };

    public static byte[] exportDetails(Long inventaireId, List<InventaireDetail> details,
            Map<Long, Double> currentStocks) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Inventaire " + inventaireId);

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < DETAIL_HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(DETAIL_HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (InventaireDetail detail : details) {
                Row row = sheet.createRow(rowIdx++);

                String materiel = detail.getMateriel() != null ? detail.getMateriel().getLibelle() : "";
                String type = detail.getMateriel() != null && detail.getMateriel().getType() != null
                        ? detail.getMateriel().getType().getLibelle() : "";
                Double qteInitiale = detail.getQuantiteInitiale() != null ? detail.getQuantiteInitiale() : 0.0;
                Double qteFinale = detail.getQuantiteFinale() != null ? detail.getQuantiteFinale() : 0.0;
                Double qteActuelle = detail.getMateriel() != null
                        ? currentStocks.getOrDefault(detail.getMateriel().getId(), 0.0)
                        : 0.0;
                double ecart = qteFinale - qteInitiale;

                row.createCell(0).setCellValue(materiel);
                row.createCell(1).setCellValue(type);
                row.createCell(2).setCellValue(qteInitiale);
                row.createCell(3).setCellValue(qteFinale);
                row.createCell(4).setCellValue(qteActuelle);
                row.createCell(5).setCellValue(ecart);
                row.createCell(6).setCellValue(
                        detail.getObservations() != null && !detail.getObservations().isEmpty()
                                ? detail.getObservations() : "R.A.S.");
            }

            for (int i = 0; i < DETAIL_HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}
