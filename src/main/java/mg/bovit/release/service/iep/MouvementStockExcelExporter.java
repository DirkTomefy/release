package mg.bovit.release.service.iep;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

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

import mg.bovit.release.model.MouvementStock;

public class MouvementStockExcelExporter {

    private static final String[] HEADERS = {
            "ID", "Date", "Matériel", "Type", "Quantité", "P.U. (Ar)", "Total estimé (Ar)", "Qté restante"
    };

    public static byte[] export(List<MouvementStock> mouvements) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Mouvements de stock");

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
            for (MouvementStock m : mouvements) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(m.getId() != null ? m.getId() : 0L);
                row.createCell(1).setCellValue(
                        m.getDateMouvement() != null ? sdf.format(m.getDateMouvement()) : "");
                row.createCell(2).setCellValue(
                        m.getMateriel() != null && m.getMateriel().getLibelle() != null
                                ? m.getMateriel().getLibelle() : "-");
                row.createCell(3).setCellValue(
                        m.getTypeMouvement() != null ? m.getTypeMouvement() : "");
                row.createCell(4).setCellValue(
                        m.getQuantite() != null ? m.getQuantite() : 0d);
                row.createCell(5).setCellValue(
                        m.getPrixUnitaire() != null ? m.getPrixUnitaire() : 0d);
                row.createCell(6).setCellValue(
                        m.getPrixUnitaire() != null && m.getQuantite() != null
                                ? m.getPrixUnitaire() * m.getQuantite() : 0d);
                row.createCell(7).setCellValue(
                        m.getQteRestant() != null ? m.getQteRestant() : 0d);
            }

            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}
