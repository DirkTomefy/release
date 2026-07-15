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

import mg.bovit.release.model.MvtCaisse;

public class MvtCaissePdfExporter {

    public static byte[] export(List<MvtCaisse> mouvements, String titre) throws DocumentException {
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

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{0.6f, 1.2f, 1.5f, 1.5f, 1.5f, 1.2f});

        Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
        String[] headers = {"ID", "Date", "Caisse", "Cause", "Montant", "Sens"};
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
        for (MvtCaisse m : mouvements) {
            Color bg = alt ? new Color(240, 245, 240) : Color.WHITE;
            alt = !alt;

            String caisse = (m.getCaisse() != null && m.getCaisse().getLibelle() != null)
                    ? m.getCaisse().getLibelle() : "-";
            String cause = (m.getCauseCaisse() != null && m.getCauseCaisse().getLibelle() != null)
                    ? m.getCauseCaisse().getLibelle() : "-";
            String sens = m.getMontant() != null && m.getMontant() >= 0 ? "Entrée" : "Sortie";

            addCell(table, String.valueOf(m.getId()), bodyFont, bg);
            addCell(table, m.getDate() != null ? sdf.format(m.getDate()) : "", bodyFont, bg);
            addCell(table, caisse, bodyFont, bg);
            addCell(table, cause, bodyFont, bg);
            addCell(table, m.getMontant() != null ? String.valueOf(m.getMontant()) : "", bodyFont, bg);
            addCell(table, sens, bodyFont, bg);
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
