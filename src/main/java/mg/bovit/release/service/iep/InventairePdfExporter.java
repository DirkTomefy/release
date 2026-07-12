package mg.bovit.release.service.iep;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import mg.bovit.release.model.Inventaire;
import mg.bovit.release.model.InventaireDetail;

public class InventairePdfExporter {

    public static byte[] export(List<Inventaire> inventaires, String titre) throws DocumentException {
        Document document = new Document(PageSize.A4.rotate(), 30, 30, 40, 30);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);

        document.open();

        Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
        Paragraph title = new Paragraph(titre, titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(15f);
        document.add(title);

        Font dateFont = new Font(Font.HELVETICA, 9, Font.ITALIC, Color.GRAY);
        Paragraph genDate = new Paragraph(
                "Généré le " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()),
                dateFont);
        genDate.setAlignment(Element.ALIGN_RIGHT);
        genDate.setSpacingAfter(10f);
        document.add(genDate);

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1f, 2f, 4f});

        Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
        String[] headers = {"ID", "Date d'inventaire", "Libellé"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(new Color(34, 100, 34));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6f);
            table.addCell(cell);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Font bodyFont = new Font(Font.HELVETICA, 9);
        boolean alt = false;
        for (Inventaire inv : inventaires) {
            Color bg = alt ? new Color(240, 245, 240) : Color.WHITE;
            alt = !alt;

            addCell(table, String.valueOf(inv.getId()), bodyFont, bg);
            addCell(table, inv.getDateInventaire() != null ? sdf.format(inv.getDateInventaire()) : "", bodyFont, bg);
            addCell(table, inv.getLibelle() != null ? inv.getLibelle() : "", bodyFont, bg);
        }

        document.add(table);
        document.close();

        return out.toByteArray();
    }

    public static byte[] exportDetails(Long inventaireId, List<InventaireDetail> details,
            Map<Long, Double> currentStocks, String titre) throws DocumentException {
        Document document = new Document(PageSize.A4.rotate(), 30, 30, 40, 30);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);

        document.open();

        Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
        Paragraph title = new Paragraph(titre, titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(15f);
        document.add(title);

        Font dateFont = new Font(Font.HELVETICA, 9, Font.ITALIC, Color.GRAY);
        Paragraph genDate = new Paragraph(
                "Généré le " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()),
                dateFont);
        genDate.setAlignment(Element.ALIGN_RIGHT);
        genDate.setSpacingAfter(10f);
        document.add(genDate);

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3f, 2f, 2f, 2f, 2f, 1.5f, 4f});

        Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
        String[] headers = {"Matériel", "Type matériel", "Qté Initiale", "Qté Réelle", "Qté Actuelle", "Écart", "Observations"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(new Color(34, 100, 34));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6f);
            table.addCell(cell);
        }

        Font bodyFont = new Font(Font.HELVETICA, 9);
        boolean alt = false;
        for (InventaireDetail detail : details) {
            Color bg = alt ? new Color(240, 245, 240) : Color.WHITE;
            alt = !alt;

            String materiel = detail.getMateriel() != null ? detail.getMateriel().getLibelle() : "";
            String type = detail.getMateriel() != null && detail.getMateriel().getType() != null
                    ? detail.getMateriel().getType().getLibelle() : "";
            Double qteInitiale = detail.getQuantiteInitiale() != null ? detail.getQuantiteInitiale() : 0.0;
            Double qteFinale = detail.getQuantiteFinale() != null ? detail.getQuantiteFinale() : 0.0;
            Double qteActuelle = detail.getMateriel() != null
                    ? currentStocks.getOrDefault(detail.getMateriel().getId(), 0.0)
                    : 0.0;
            double ecart = qteFinale - qteInitiale;

            addCell(table, materiel, bodyFont, bg);
            addCell(table, type, bodyFont, bg);
            addCell(table, String.valueOf(qteInitiale), bodyFont, bg);
            addCell(table, String.valueOf(qteFinale), bodyFont, bg);
            addCell(table, String.valueOf(qteActuelle), bodyFont, bg);
            addCell(table, String.valueOf(ecart), bodyFont, bg);
            addCell(table, detail.getObservations() != null && !detail.getObservations().isEmpty()
                    ? detail.getObservations() : "R.A.S.", bodyFont, bg);
        }

        document.add(table);
        document.close();

        return out.toByteArray();
    }

    private static void addCell(PdfPTable table, String text, Font font, Color bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setPadding(5f);
        table.addCell(cell);
    }
}
