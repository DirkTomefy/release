package mg.bovit.release.service.iep;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
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

import mg.bovit.release.dto.CaisseStatDTO;

public class CaisseStatPdfExporter {

    private static final DecimalFormat MONTANT = new DecimalFormat("#,##0");

    public static byte[] export(CaisseStatDTO stats, String periode, String titre) throws DocumentException {
        Document document = new Document(PageSize.A4, 30, 30, 40, 30);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);

        document.open();

        Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
        Paragraph title = new Paragraph(titre, titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(5f);
        document.add(title);

        Font periodeFont = new Font(Font.HELVETICA, 11);
        Paragraph periodeP = new Paragraph(periode != null ? periode : "", periodeFont);
        periodeP.setAlignment(Element.ALIGN_CENTER);
        periodeP.setSpacingAfter(15f);
        document.add(periodeP);

        Font bodyFont = new Font(Font.HELVETICA, 10);
        Font synthFont = new Font(Font.HELVETICA, 10, Font.BOLD);
        document.add(new Paragraph("Total entrées : " + formatMontant(stats.getTotalEntree()), bodyFont));
        document.add(new Paragraph("Total sorties : " + formatMontant(stats.getTotalSortie()), bodyFont));
        document.add(new Paragraph("Solde : " + formatMontant(stats.getSolde()), synthFont));
        document.add(new Paragraph(" ", bodyFont));

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3f, 2f, 2f});

        Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
        String[] headers = {"Période", "Entrées", "Sorties"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(new Color(34, 100, 34));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6f);
            table.addCell(cell);
        }

        List<String> labels = stats.getLabels();
        List<Double> entrees = stats.getEntrees();
        List<Double> sorties = stats.getSorties();
        boolean alt = false;
        int size = labels != null ? labels.size() : 0;
        for (int i = 0; i < size; i++) {
            Color bg = alt ? new Color(240, 245, 240) : Color.WHITE;
            alt = !alt;

            addCell(table, labels.get(i), bodyFont, bg);
            addCell(table, formatMontant(valeur(entrees, i)), bodyFont, bg);
            addCell(table, formatMontant(valeur(sorties, i)), bodyFont, bg);
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
