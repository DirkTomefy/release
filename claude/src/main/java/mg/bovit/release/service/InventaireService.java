package mg.bovit.release.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mg.bovit.release.dto.InventaireDetailPayload;
import mg.bovit.release.dto.InventairePayload;
import mg.bovit.release.dto.MouvementPaiementPayload;
import mg.bovit.release.dto.MouvementStockPayload;
import mg.bovit.release.model.Inventaire;
import mg.bovit.release.model.InventaireDetail;
import mg.bovit.release.model.Materiel;
import mg.bovit.release.repository.InventaireDetailRepository;
import mg.bovit.release.repository.InventaireRepository;
import mg.bovit.release.repository.MaterielRepository;

@Service
public class InventaireService {
    @Autowired
    private MouvementStockService mouvementStockService;
    @Autowired
    private InventaireDetailService inventaireDetailService;
    @Autowired
    private InventaireRepository inventaireRepository;
    @Autowired
    private InventaireDetailRepository inventaireDetailRepository;
    @Autowired
    private MaterielRepository materielRepository;

    @Transactional
    public void faireInventaireMultiple(InventairePayload payload) {
        // sauvegarde du parent 'inventaire'
        Inventaire inventaire = new Inventaire();
        inventaire.setDateInventaire(java.sql.Date.valueOf(payload.getDateInventaire()));
        inventaire.setLibelle(payload.getLibelle() != null && !payload.getLibelle().isEmpty() 
            ? payload.getLibelle() 
            : "Inventaire global de regularisation");
        inventaire = inventaireRepository.save(inventaire);

        // sauvegarde de chaque ligne d'inventaire et generation des mouvements de stock
        for (InventaireDetailPayload detailPayload : payload.getDetails()) {
            Double qteInitiale = detailPayload.getQuantiteInitiale() != null ? detailPayload.getQuantiteInitiale() : 0.0;
            Double qteFinale = detailPayload.getQuantiteFinale() != null ? detailPayload.getQuantiteFinale() : 0.0;
            Long matId = detailPayload.getMaterielId();

            if (qteFinale == null) continue; // On ignore les lignes non remplies

            // Sauvegarde de la ligne d'historique
            InventaireDetail detail = new InventaireDetail();
            detail.setInventaire(inventaire);
            
            Materiel materiel = materielRepository.findById(matId).orElse(null);
            detail.setMateriel(materiel);
            detail.setQuantiteInitiale(qteInitiale);
            detail.setQuantiteFinale(qteFinale);
            detail.setObservations(detailPayload.getObservations());
            inventaireDetailRepository.save(detail);

            // Generation dynamique des flux de regularisation
            if (qteFinale > qteInitiale) {
                MouvementStockPayload stockPayload = new MouvementStockPayload();
                stockPayload.setTypeMouvement("ENTREE");
                stockPayload.setMaterielId(matId);
                stockPayload.setQuantite(qteFinale - qteInitiale);
                stockPayload.setDateMouvement(payload.getDateInventaire());
                stockPayload.setPrixUnitaire(0.0);

                List<MouvementPaiementPayload> payments = new ArrayList<>();
                payments.add(new MouvementPaiementPayload((long) 1, 0.0));
                stockPayload.setPayments(payments);

                mouvementStockService.traiterMouvementStock(stockPayload);

            } else if (qteFinale < qteInitiale) {
                MouvementStockPayload stockPayload = new MouvementStockPayload();
                stockPayload.setTypeMouvement("SORTIE");
                stockPayload.setMaterielId(matId);
                stockPayload.setQuantite(qteInitiale - qteFinale);
                stockPayload.setDateMouvement(payload.getDateInventaire());
                stockPayload.setPrixUnitaire(null);
                stockPayload.setPayments(new ArrayList<>());

                mouvementStockService.traiterMouvementStock(stockPayload);
            }
        }
    }

    public List<Inventaire> listerInventaires() {
        return inventaireRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Inventaire findById(Long id) {
        return inventaireRepository.findById(id).orElse(null);
    }

    public List<InventaireDetail> listerInventairesDetailsParId(Long id) {
        return inventaireDetailRepository.findAll().stream()
                .filter(detail -> detail.getInventaire() != null && detail.getInventaire().getId().equals(id))
                .toList();
    }

    public Inventaire saveFromPaylod(InventairePayload payload) {
        Inventaire inventaire = new Inventaire();
        inventaire.setDateInventaire(java.sql.Date.valueOf(payload.getDateInventaire()));
        inventaire.setLibelle(payload.getLibelle());
        return inventaireRepository.save(inventaire);
    }

    @Transactional
    public void saveInventaireAndDetails(InventairePayload payload) {
        Inventaire inventaire = saveFromPaylod(payload);
        inventaireDetailService.saveFromPayload(payload, inventaire);
    }
}