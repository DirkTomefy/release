package mg.bovit.release.service.iep;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;

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

import mg.bovit.release.model.MouvementStock;

public class MouvementStockPdfExporter {

    public static byte[] export(List<MouvementStock> mouvements, String titre) throws DocumentException {
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

        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{0.6f, 1.2f, 1.5f, 1f, 1f, 1.2f, 1.5f, 1.2f});

        Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
        String[] headers = {"ID", "Date", "Matériel", "Type", "Quantité", "P.U. (Ar)", "Total estimé (Ar)", "Qté restante"};
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
        for (MouvementStock m : mouvements) {
            Color bg = alt ? new Color(240, 245, 240) : Color.WHITE;
            alt = !alt;

            String materiel = (m.getMateriel() != null && m.getMateriel().getLibelle() != null)
                    ? m.getMateriel().getLibelle() : "-";

            addCell(table, String.valueOf(m.getId()), bodyFont, bg);
            addCell(table, m.getDateMouvement() != null ? sdf.format(m.getDateMouvement()) : "", bodyFont, bg);
            addCell(table, materiel, bodyFont, bg);
            addCell(table, m.getTypeMouvement() != null ? m.getTypeMouvement() : "", bodyFont, bg);
            addCell(table, m.getQuantite() != null ? String.valueOf(m.getQuantite()) : "", bodyFont, bg);
            addCell(table, m.getPrixUnitaire() != null ? String.valueOf(m.getPrixUnitaire()) : "-", bodyFont, bg);
            addCell(table, m.getPrixUnitaire() != null && m.getQuantite() != null
                    ? String.valueOf(m.getPrixUnitaire() * m.getQuantite()) : "0", bodyFont, bg);
            addCell(table, m.getQteRestant() != null ? String.valueOf(m.getQteRestant()) : "", bodyFont, bg);
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
