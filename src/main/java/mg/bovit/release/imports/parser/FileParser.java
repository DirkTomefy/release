package mg.bovit.release.imports.parser;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileParser {

    public static List<Map<String, String>> parseCsv(MultipartFile file) throws Exception {
        List<Map<String, String>> rows = new ArrayList<>();
        try (InputStream is = file.getInputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String headerLine = null;
            while ((headerLine = br.readLine()) != null) {
                if (headerLine.trim().isEmpty()) continue;
                break;
            }
            if (headerLine == null) return rows;
            String[] headers = splitCsvLine(headerLine);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] cols = splitCsvLine(line);
                Map<String, String> map = new HashMap<>();
                for (int i = 0; i < headers.length && i < cols.length; i++) {
                    map.put(headers[i].trim(), cols[i].trim());
                }
                rows.add(map);
            }
        }
        return rows;
    }

    private static String[] splitCsvLine(String line) {
        if (line.contains(";")) return line.split(";");
        return line.split(",");
    }

    public static List<Map<String, String>> parseExcel(MultipartFile file) throws Exception {
        List<Map<String, String>> rows = new ArrayList<>();
        try (InputStream is = file.getInputStream(); Workbook wb = new XSSFWorkbook(is)) {
            Sheet sheet = wb.getSheetAt(0);
            if (sheet == null) return rows;
            Row headerRow = sheet.getRow(sheet.getFirstRowNum());
            if (headerRow == null) return rows;
            int lastCol = headerRow.getLastCellNum();
            List<String> headers = new ArrayList<>();
            for (int c = 0; c < lastCol; c++) {
                if (headerRow.getCell(c) != null)
                    headers.add(headerRow.getCell(c).toString().trim());
                else headers.add("col" + c);
            }
            for (int r = sheet.getFirstRowNum() + 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;
                Map<String, String> map = new HashMap<>();
                for (int c = 0; c < headers.size(); c++) {
                    if (row.getCell(c) != null)
                        map.put(headers.get(c), row.getCell(c).toString().trim());
                    else map.put(headers.get(c), "");
                }
                rows.add(map);
            }
        }
        return rows;
    }
}
