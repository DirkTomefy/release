package mg.bovit.release.service.iep;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
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

import mg.bovit.release.dto.CaisseStatDTO;

public class CaisseStatExcelExporter {

    private static final DecimalFormat MONTANT = new DecimalFormat("#,##0");
    private static final String[] HEADERS = { "Période", "Entrées", "Sorties" };

    public static byte[] export(CaisseStatDTO stats, String periode) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Statistiques caisse");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleStyle.setFont(titleFont);

            // Titre + période
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Statistiques de caisse");
            titleCell.setCellStyle(titleStyle);

            Row periodeRow = sheet.createRow(1);
            periodeRow.createCell(0).setCellValue(periode != null ? periode : "");

            // Bloc de synthèse
            int synthBase = 3;
            Row r = sheet.createRow(synthBase);
            r.createCell(0).setCellValue("Total entrées");
            r.createCell(1).setCellValue(formatMontant(stats.getTotalEntree()));
            r = sheet.createRow(synthBase + 1);
            r.createCell(0).setCellValue("Total sorties");
            r.createCell(1).setCellValue(formatMontant(stats.getTotalSortie()));
            r = sheet.createRow(synthBase + 2);
            r.createCell(0).setCellValue("Solde");
            r.createCell(1).setCellValue(formatMontant(stats.getSolde()));

            // Tableau détaillé par intervalle
            int headerIdx = synthBase + 4;
            Row headerRow = sheet.createRow(headerIdx);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            List<String> labels = stats.getLabels();
            List<Double> entrees = stats.getEntrees();
            List<Double> sorties = stats.getSorties();
            int rowIdx = headerIdx + 1;
            int size = labels != null ? labels.size() : 0;
            for (int i = 0; i < size; i++) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(labels.get(i));
                row.createCell(1).setCellValue(valeur(entrees, i));
                row.createCell(2).setCellValue(valeur(sorties, i));
            }

            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private static double valeur(List<Double> liste, int i) {
        if (liste != null && i < liste.size() && liste.get(i) != null) {
            return liste.get(i);
        }
        return 0.0;
    }

    private static String formatMontant(Double val) {
        return MONTANT.format(val != null ? val : 0.0) + " Ar";
    }
}
