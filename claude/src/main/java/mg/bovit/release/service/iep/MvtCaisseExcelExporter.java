package mg.bovit.release.service.iep;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Date;
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

import mg.bovit.release.model.MvtCaisse;

public class MvtCaisseExcelExporter {

    private static final String[] HEADERS = {
            "ID", "Date", "Caisse", "Cause", "Montant", "Sens"
    };

    public static byte[] export(List<MvtCaisse> mouvements) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Mouvements de caisse");

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
            for (MvtCaisse m : mouvements) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(m.getId() != null ? m.getId() : 0L);
                row.createCell(1).setCellValue(
                        m.getDate() != null ? sdf.format(m.getDate()) : "");
                row.createCell(2).setCellValue(
                        m.getCaisse() != null && m.getCaisse().getLibelle() != null
                                ? m.getCaisse().getLibelle() : "-");
                row.createCell(3).setCellValue(
                        m.getCauseCaisse() != null && m.getCauseCaisse().getLibelle() != null
                                ? m.getCauseCaisse().getLibelle() : "-");
                row.createCell(4).setCellValue(
                        m.getMontant() != null ? m.getMontant() : 0d);
                String sens = m.getMontant() != null && m.getMontant() >= 0 ? "Entrée" : "Sortie";
                row.createCell(5).setCellValue(sens);
            }

            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}
