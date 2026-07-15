package mg.bovit.release.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

import mg.bovit.release.model.Bovin;
import mg.bovit.release.model.Facture;
import mg.bovit.release.model.FactureDetail;
import mg.bovit.release.model.VenteBovin;
import mg.bovit.release.model.VenteDetail;
import mg.bovit.release.repository.FactureDetailRepository;
import mg.bovit.release.repository.FactureRepository;
import mg.bovit.release.repository.VenteBovinRepository;
import mg.bovit.release.repository.VenteDetailRepository;

@Service
public class FactureService {

    private final FactureRepository factureRepository;
    private final FactureDetailRepository factureDetailRepository;
    private final VenteBovinRepository venteBovinRepository;
    private final VenteDetailRepository venteDetailRepository;

    public FactureService(FactureRepository factureRepository,
                          FactureDetailRepository factureDetailRepository,
                          VenteBovinRepository venteBovinRepository,
                          VenteDetailRepository venteDetailRepository) {
        this.factureRepository = factureRepository;
        this.factureDetailRepository = factureDetailRepository;
        this.venteBovinRepository = venteBovinRepository;
        this.venteDetailRepository = venteDetailRepository;
    }

    // Vérifie si une facture existe
    public boolean existsByVenteId(Long idVente) {
        return factureRepository.existsByVente_Id(idVente);
    }

    // Récupère une facture par ID de vente
    public Optional<Facture> findByVenteId(Long idVente) {
        return factureRepository.findByVente_Id(idVente);
    }

    // Récupère une facture par son code
    public Optional<Facture> findByCodeFacture(String codeFacture) {
        return factureRepository.findByCodeFacture(codeFacture);
    }

    // Télécharge le PDF d'une facture existante par son code
    public byte[] telechargerPdf(String codeFacture) throws Exception {
        Facture facture = factureRepository.findByCodeFacture(codeFacture)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée avec le code : " + codeFacture));
        return generatePdf(facture);
    }

    // Télécharge le PDF d'une facture existante par son ID
    public byte[] telechargerPdfById(Long idFacture) throws Exception {
        Facture facture = factureRepository.findById(idFacture)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée avec l'ID : " + idFacture));
        return generatePdf(facture);
    }

    // Génère une facture à partir d'une vente
    @Transactional
    public byte[] genererFacture(Long idVente) throws Exception {
        VenteBovin vente = venteBovinRepository.findById(idVente)
                .orElseThrow(() -> new RuntimeException("Vente non trouvée"));

        if (factureRepository.existsByVente_Id(idVente)) {
            throw new RuntimeException("Une facture existe déjà pour cette vente");
        }

        List<VenteDetail> detailsVente = venteDetailRepository.findByVenteBovin_Id(idVente);
        if (detailsVente.isEmpty()) {
            throw new RuntimeException("Aucun bovin associé à cette vente");
        }

        // Construction de la facture
        Facture facture = new Facture();
        facture.setVente(vente);
        facture.setDateFacture(LocalDate.now());

        // Génération du code facture
        String codeFacture = genererCodeFacture(vente.getId());
        facture.setCodeFacture(codeFacture);
        facture.setNumeroFacture("FACT-" + System.currentTimeMillis());

        // Calcul du montant total
        double total = 0.0;
        for (VenteDetail vd : detailsVente) {
            Bovin bovin = vd.getBovin();
            if (bovin.getPrix_vente() == null) {
                throw new RuntimeException("Le bovin " + bovin.getId() + " n'a pas de prix de vente défini");
            }
            total += bovin.getPrix_vente();
        }
        facture.setMontantTotal(total);

        facture = factureRepository.save(facture);

        // Création des détails
        for (VenteDetail vd : detailsVente) {
            FactureDetail fd = new FactureDetail();
            fd.setVenteDetail(vd);
            fd.setPrixUnitaire(vd.getBovin().getPrix_vente());
            fd.setQuantite(1);
            facture.addDetail(fd);
            factureDetailRepository.save(fd);
        }

        return generatePdf(facture);
    }

    // Génération du code facture
    private String genererCodeFacture(Long idVente) {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();

        Long count = factureRepository.countByMonthAndYear(month, year);
        long apparition = (count != null ? count : 0) + 1;

        return String.format("fact_%02d_%04d_%03d_%d",
                month, year, apparition, idVente);
    }

    // Génération du PDF (méthode publique pour le téléchargement)
    // Génération du PDF (méthode publique pour le téléchargement)
    public byte[] generatePdf(Facture facture) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);
        document.setMargins(40, 40, 40, 40);
    
        // Polices
        PdfFont fontRegular = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
    
        // Palette de couleurs (cohérente avec le reste de l'application)
        DeviceRgb couleurPrimaire = new DeviceRgb(31, 138, 74);
        DeviceRgb couleurTexteClair = new DeviceRgb(255, 255, 255);
        DeviceRgb couleurGrisClair = new DeviceRgb(245, 248, 246);
        DeviceRgb couleurGrisTexte = new DeviceRgb(90, 90, 90);
        DeviceRgb couleurBordure = new DeviceRgb(220, 232, 224);
    
        // ---------- EN-TÊTE ----------
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 1})).useAllAvailableWidth();
        headerTable.setBorder(Border.NO_BORDER);
    
        Cell logoCell = new Cell()
                .add(new Paragraph("BOVIT").setFont(fontBold).setFontSize(20).setFontColor(couleurPrimaire))
                .add(new Paragraph("Gestion d'élevage bovin").setFont(fontRegular).setFontSize(9).setFontColor(couleurGrisTexte))
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
    
        Cell titreCell = new Cell()
                .add(new Paragraph("FACTURE").setFont(fontBold).setFontSize(24).setFontColor(couleurPrimaire).setTextAlignment(TextAlignment.RIGHT))
                .add(new Paragraph("N° " + facture.getNumeroFacture()).setFont(fontRegular).setFontSize(10).setTextAlignment(TextAlignment.RIGHT))
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
    
        headerTable.addCell(logoCell);
        headerTable.addCell(titreCell);
        document.add(headerTable);
    
        document.add(new LineSeparator(new SolidLine(1.2f))
                .setMarginTop(10).setMarginBottom(15).setStrokeColor(couleurPrimaire));
    
        // ---------- INFOS FACTURE / CLIENT ----------
        Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1, 1})).useAllAvailableWidth();
        infoTable.setBorder(Border.NO_BORDER);
    
        Cell infoFactureCell = new Cell()
                .setBorder(Border.NO_BORDER)
                .add(ligneLibelleValeur("Code facture", facture.getCodeFacture(), fontBold, fontRegular, couleurGrisTexte))
                .add(ligneLibelleValeur("Date", facture.getDateFacture().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), fontBold, fontRegular, couleurGrisTexte));
    
        Cell infoClientCell = new Cell()
                .setBorder(Border.NO_BORDER)
                .add(ligneLibelleValeur("Client",
                        facture.getVente().getClient().getNom() + " " + facture.getVente().getClient().getPrenom(),
                        fontBold, fontRegular, couleurGrisTexte))
                .add(ligneLibelleValeur("Contact", facture.getVente().getClient().getContact(), fontBold, fontRegular, couleurGrisTexte));
    
        infoTable.addCell(infoFactureCell);
        infoTable.addCell(infoClientCell);
        document.add(infoTable);
        document.add(new Paragraph("\n"));
    
        // ---------- TABLEAU DES BOVINS ----------
        Table table = new Table(UnitValue.createPercentArray(new float[]{10, 45, 20, 25})).useAllAvailableWidth();
    
        table.addHeaderCell(enteteCellule("N°", fontBold, couleurPrimaire, couleurTexteClair, TextAlignment.CENTER));
        table.addHeaderCell(enteteCellule("Bovin (race)", fontBold, couleurPrimaire, couleurTexteClair, TextAlignment.LEFT));
        table.addHeaderCell(enteteCellule("Prix unitaire", fontBold, couleurPrimaire, couleurTexteClair, TextAlignment.RIGHT));
        table.addHeaderCell(enteteCellule("Total", fontBold, couleurPrimaire, couleurTexteClair, TextAlignment.RIGHT));
    
        int i = 1;
        for (FactureDetail fd : facture.getDetails()) {
            DeviceRgb fondLigne = (i % 2 == 0) ? couleurGrisClair : new DeviceRgb(255, 255, 255);
            String race = fd.getVenteDetail().getBovin().getRace() != null
                    ? fd.getVenteDetail().getBovin().getRace().getNom()
                    : "-";
            double totalLigne = fd.getPrixUnitaire() * fd.getQuantite();
    
            table.addCell(donneeCellule(String.valueOf(i), fontRegular, fondLigne, TextAlignment.CENTER));
            table.addCell(donneeCellule(race, fontRegular, fondLigne, TextAlignment.LEFT));
            table.addCell(donneeCellule(formatMontant(fd.getPrixUnitaire()), fontRegular, fondLigne, TextAlignment.RIGHT));
            table.addCell(donneeCellule(formatMontant(totalLigne), fontRegular, fondLigne, TextAlignment.RIGHT));
            i++;
        }
        document.add(table);
    
        // ---------- TOTAL GÉNÉRAL ----------
        Table totalTable = new Table(UnitValue.createPercentArray(new float[]{55, 20, 25})).useAllAvailableWidth();
        totalTable.setMarginTop(10);
    
        totalTable.addCell(new Cell().setBorder(Border.NO_BORDER));
        totalTable.addCell(new Cell()
                .add(new Paragraph("TOTAL").setFont(fontBold).setFontSize(12))
                .setBorder(Border.NO_BORDER)
                .setBackgroundColor(couleurGrisClair)
                .setPadding(8)
                .setTextAlignment(TextAlignment.RIGHT));
        totalTable.addCell(new Cell()
                .add(new Paragraph(formatMontant(facture.getMontantTotal())).setFont(fontBold).setFontSize(12).setFontColor(couleurPrimaire))
                .setBorder(Border.NO_BORDER)
                .setBackgroundColor(couleurGrisClair)
                .setPadding(8)
                .setTextAlignment(TextAlignment.RIGHT));
        document.add(totalTable);
    
        // ---------- PIED DE PAGE ----------
        document.add(new Paragraph("\n"));
        document.add(new LineSeparator(new SolidLine(0.5f)).setStrokeColor(couleurBordure).setMarginTop(20));
        document.add(new Paragraph("Merci pour votre confiance.")
                .setFont(fontRegular).setFontSize(9).setFontColor(couleurGrisTexte)
                .setTextAlignment(TextAlignment.CENTER).setMarginTop(10));
    
        document.close();
        return baos.toByteArray();
    }
    
    // ---------- Méthodes utilitaires de mise en forme ----------
    
    private Paragraph ligneLibelleValeur(String libelle, String valeur, PdfFont fontBold, PdfFont fontRegular, DeviceRgb couleurLibelle) {
        return new Paragraph()
                .add(new Text(libelle + " : ").setFont(fontBold).setFontSize(9).setFontColor(couleurLibelle))
                .add(new Text(valeur != null ? valeur : "-").setFont(fontRegular).setFontSize(10))
                .setMarginBottom(4);
    }
    
    private Cell enteteCellule(String texte, PdfFont font, DeviceRgb fond, DeviceRgb couleurTexte, TextAlignment align) {
        return new Cell()
                .add(new Paragraph(texte).setFont(font).setFontColor(couleurTexte).setFontSize(10))
                .setBackgroundColor(fond)
                .setPadding(8)
                .setTextAlignment(align)
                .setBorder(Border.NO_BORDER);
    }
    
    private Cell donneeCellule(String texte, PdfFont font, DeviceRgb fond, TextAlignment align) {
        return new Cell()
                .add(new Paragraph(texte).setFont(font).setFontSize(10))
                .setBackgroundColor(fond)
                .setPadding(7)
                .setTextAlignment(align)
                .setBorderBottom(new SolidBorder(new DeviceRgb(230, 230, 230), 0.5f))
                .setBorderTop(Border.NO_BORDER)
                .setBorderLeft(Border.NO_BORDER)
                .setBorderRight(Border.NO_BORDER);
    }
    
    private String formatMontant(double montant) {
        return String.format("%,.2f", montant).replace(",", " ");
    }
}