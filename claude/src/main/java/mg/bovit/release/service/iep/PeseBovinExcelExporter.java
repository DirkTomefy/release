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

import mg.bovit.release.model.sqlview.PeseBovinWithDateVente;

/**
 * Génère un classeur Excel (.xlsx) à partir d'une liste de pesées de bovins.
 * Utilisé aussi bien pour l'export d'une seule pesée (liste à un élément)
 * que pour l'export complet de la liste filtrée.
 */
public class PeseBovinExcelExporter {

    private static final String[] HEADERS = {
            "ID", "ID Bovin", "Date pesée", "Poids (kg)", "Date de vente"
    };

    public static byte[] export(List<PeseBovinWithDateVente> pesees) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Pesées bovins");

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
            for (PeseBovinWithDateVente p : pesees) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(p.getId());
                row.createCell(1).setCellValue(
                        p.getBovin() != null && p.getBovin().getId() != null
                                ? p.getBovin().getId().doubleValue() : 0d);
                row.createCell(2).setCellValue(
                        p.getDate_pese() != null ? sdf.format(p.getDate_pese()) : "");
                row.createCell(3).setCellValue(
                        p.getPoids_apres() != null ? p.getPoids_apres() : 0d);
                row.createCell(4).setCellValue(
                        p.getDate_vente() != null ? sdf.format(p.getDate_vente()) : "-");
            }

            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}
