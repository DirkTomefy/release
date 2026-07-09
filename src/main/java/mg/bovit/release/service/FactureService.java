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

    /**
     * Génère une facture à partir d'une vente existante.
     * Calcule le code_facture unique, crée les détails, sauvegarde et retourne le PDF.
     */
    @Transactional
    public byte[] genererFacture(Long idVente) throws Exception {
        // 1. Récupérer la vente
        VenteBovin vente = venteBovinRepository.findById(idVente)
                .orElseThrow(() -> new RuntimeException("Vente non trouvée"));

        // Vérifier si une facture existe déjà
        if (factureRepository.findByVente(vente).isPresent()) {
            throw new RuntimeException("Une facture existe déjà pour cette vente");
        }

        // 2. Récupérer les détails de la vente
        List<VenteDetail> detailsVente = venteDetailRepository.findByVenteBovin_Id(idVente);
        if (detailsVente.isEmpty()) {
            throw new RuntimeException("Aucun bovin associé à cette vente");
        }

        // 3. Construire la facture
        Facture facture = new Facture();
        facture.setVente(vente);
        facture.setDateFacture(LocalDate.now());

        // Calcul du code_facture
        String codeFacture = genererCodeFacture(vente.getId());
        facture.setCodeFacture(codeFacture);

        // Numéro de facture séquentiel (simple) : on peut prendre un timestamp ou un incrément
        String numero = "FACT-" + System.currentTimeMillis(); // à améliorer selon besoin
        facture.setNumeroFacture(numero);

        // Calcul du montant total (on suppose que le prix de vente est dans bovin.prixVente)
        double total = 0.0;
        for (VenteDetail vd : detailsVente) {
            Bovin bovin = vd.getBovin();
            if (bovin.getPoids_vente() == null) {
                throw new RuntimeException("Le bovin " + bovin.getId() + " n'a pas de prix de vente défini");
            }
            total += bovin.getPrix_vente(); // par défaut, quantite = 1
        }
        facture.setMontantTotal(total);

        // Sauvegarder la facture (pour avoir un ID)
        facture = factureRepository.save(facture);

        // 4. Créer les détails de facture
        for (VenteDetail vd : detailsVente) {
            FactureDetail fd = new FactureDetail();
            fd.setFacture(facture);
            fd.setVenteDetail(vd);
            fd.setPrixUnitaire(vd.getBovin().getPrix_vente());
            fd.setQuantite(1);
            factureDetailRepository.save(fd);
        }

        // 5. Générer le PDF
        return genererPdf(facture);
    }

    /**
     * Génère le code_facture selon le format : fact_MM_AAAA_XXX_IDVENTE
     * où XXX est le numéro d'apparition dans le mois (ordre croissant des factures du mois).
     */
    private String genererCodeFacture(Long idVente) {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();

        // Compter combien de factures ont déjà été créées dans ce mois-année
        Long count = factureRepository.countByMonthAndYear(month, year);
        long apparition = (count != null ? count : 0) + 1;

        // Format : fact_MM_AAAA_XXX_IDVENTE
        return String.format("fact_%02d_%04d_%03d_%d",
                month, year, apparition, idVente);
    }

    /**
     * Génère un PDF pour une facture donnée (retourne le tableau d'octets).
     */
    private byte[] genererPdf(Facture facture) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Titre
        document.add(new Paragraph("FACTURE")
                .setFontSize(20));

        // Infos facture
        document.add(new Paragraph("N° facture : " + facture.getNumeroFacture()));
        document.add(new Paragraph("Code : " + facture.getCodeFacture()));
        document.add(new Paragraph("Date : " + facture.getDateFacture().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        document.add(new Paragraph("Client : " + facture.getVente().getClient().getNom() + " " + facture.getVente().getClient().getPrenom()));
        document.add(new Paragraph("Contact : " + facture.getVente().getClient().getContact()));
        document.add(new Paragraph("\n"));

        // Tableau des lignes
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

        // Ligne total
        table.addCell("");
        table.addCell("");
        table.addCell("Total général");
        table.addCell(String.format("%.2f", facture.getMontantTotal()));

        document.add(table);
        document.close();

        return baos.toByteArray();
    }

    /**
     * Télécharger le PDF d'une facture existante (par son code).
     */
    public byte[] telechargerPdf(String codeFacture) throws Exception {
        Facture facture = factureRepository.findByCodeFacture(codeFacture)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée"));
        return genererPdf(facture);
    }

    public Facture getFactureByCode(String codeFacture) {
        return factureRepository.findByCodeFacture(codeFacture)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée"));
    }
}