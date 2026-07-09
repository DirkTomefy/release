package mg.bovit.release.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import mg.bovit.release.model.*;
import mg.bovit.release.repository.FactureDetailRepository;
import mg.bovit.release.repository.FactureRepository;
import mg.bovit.release.repository.VenteBovinRepository;
import mg.bovit.release.repository.VenteDetailRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

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
            fd.setFacture(facture);
            fd.setVenteDetail(vd);
            fd.setPrixUnitaire(vd.getBovin().getPrix_vente());
            fd.setQuantite(1);
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
    public byte[] generatePdf(Facture facture) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Titre
        document.add(new Paragraph("FACTURE")
                .setFontSize(20));

        // Informations
        document.add(new Paragraph("N° facture : " + facture.getNumeroFacture()));
        document.add(new Paragraph("Code : " + facture.getCodeFacture()));
        document.add(new Paragraph("Date : " + facture.getDateFacture().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        document.add(new Paragraph("Client : " + facture.getVente().getClient().getNom() + " " + facture.getVente().getClient().getPrenom()));
        document.add(new Paragraph("Contact : " + facture.getVente().getClient().getContact()));
        document.add(new Paragraph("\n"));

        // Tableau
        Table table = new Table(4);
        table.addCell("N°");
        table.addCell("Bovin (race)");
        table.addCell("Prix unitaire");
        table.addCell("Total");

        int i = 1;
        for (FactureDetail fd : facture.getDetails()) {
            table.addCell(String.valueOf(i++));
            table.addCell(fd.getVenteDetail().getBovin().getRace().getNom());
            table.addCell(String.format("%.2f", fd.getPrixUnitaire()));
            table.addCell(String.format("%.2f", fd.getPrixUnitaire() * fd.getQuantite()));
        }

        table.addCell("");
        table.addCell("");
        table.addCell("Total général");
        table.addCell(String.format("%.2f", facture.getMontantTotal()));

        document.add(table);
        document.close();

        return baos.toByteArray();
    }
}