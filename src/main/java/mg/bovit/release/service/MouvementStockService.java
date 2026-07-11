package mg.bovit.release.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mg.bovit.release.dto.MaterielStockDto;
import mg.bovit.release.dto.MouvementPaiementPayload;
import mg.bovit.release.dto.MouvementStockPayload;
import mg.bovit.release.dto.MultiCriteriaEtatStockMateriel;
import mg.bovit.release.model.Caisse;
import mg.bovit.release.model.Materiel;
import mg.bovit.release.model.MouvementCaisse;
import mg.bovit.release.model.MouvementStock;
import mg.bovit.release.model.MouvementStockPaiement;
import mg.bovit.release.repository.CaisseRepository;
import mg.bovit.release.repository.MaterielRepository;
import mg.bovit.release.repository.MouvementStockPaiementRepository;
import mg.bovit.release.repository.MouvementStockRepository;
import mg.bovit.release.specification.MouvementStockSpecification;

@Service
public class MouvementStockService {

    @Autowired
    private MouvementStockRepository mouvementStockRepository;

    @Autowired
    private MouvementStockPaiementRepository mvtStockPaiementRepository;

    @Autowired
    private MaterielRepository materielRepository;

    @Autowired
    private CaisseRepository caisseRepository;

    @Autowired
    private MouvementCaisseService mouvementCaisseService;

    public Map<LocalDate, MaterielStockDto> searchEtatStock(MultiCriteriaEtatStockMateriel form) {
        HashMap<LocalDate, MaterielStockDto> materielStockOnDates = new HashMap<>();
        // recuperer le stock restant au date initial
        MaterielStockDto matStockRestant = this.findMaterielStockRestant(form.getDateDebut(),
                form.getIdMateriel().longValue());
        // recueperer la liste des mouvements selons le critaire
        List<MouvementStock> mouvementStocks = searchMouvementStock(form);

        // ajouter le premier element
        materielStockOnDates.put(form.getDateDebut(), matStockRestant);

        Double quantiteRest = matStockRestant.getQuantiteRestant();
        for (MouvementStock mouvementStock : mouvementStocks) {
            if (mouvementStock.getTypeMouvement().equalsIgnoreCase("ENTREE")) {
                quantiteRest += mouvementStock.getQuantite();
            } else {
                quantiteRest -= mouvementStock.getQuantite();
            }
            materielStockOnDates.put(mouvementStock.getDateMouvement().toLocalDate(),
                    new MaterielStockDto(matStockRestant.getMateriel(), quantiteRest));
        }
        return new TreeMap<>(materielStockOnDates);
    }

    public List<MouvementStock> searchMouvementStock(MultiCriteriaEtatStockMateriel form) {
        Sort sort = Sort.by(Sort.Direction.ASC, "dateMouvement");
        return mouvementStockRepository.findAll(MouvementStockSpecification.fromForm(form), sort);
    }

    public MaterielStockDto findMaterielStockRestant(LocalDate date, Long idMateriel) {
        // si le materiel est introuvable retourner exeption
        if (!materielRepository.existsById(idMateriel)) {
            throw new RuntimeException("Le materiel avec id : " + idMateriel + " est introuvable .");
        }

        //calculer le reste 
        double reste  =  mouvementStockRepository.findSommeEntreeToDate(idMateriel,Date.valueOf(date)) - mouvementStockRepository.findSommeSortieToDate(idMateriel,Date.valueOf(date));
        return new MaterielStockDto(materielRepository.findById(idMateriel).get(),reste);
    }

    @Transactional
    public void traiterMouvementStock(MouvementStockPayload payload) {
        if ("ENTREE".equalsIgnoreCase(payload.getTypeMouvement())) {
            enregistrerEntreeEtPaiements(payload);
        } else if ("SORTIE".equalsIgnoreCase(payload.getTypeMouvement())) {
            enregistrerSortieEtMettreAJourEntrees(payload);
        } else {
            throw new RuntimeException("Type de mouvement inconnu : " + payload.getTypeMouvement());
        }
    }

    private void enregistrerEntreeEtPaiements(MouvementStockPayload payload) {
        // 1. Sauvegarde du mouvement principal
        MouvementStock mouvement = new MouvementStock();
        Materiel materiel = materielRepository.getReferenceById(payload.getMaterielId());

        mouvement.setMateriel(materiel);
        mouvement.setTypeMouvement("ENTREE");
        mouvement.setQuantite(payload.getQuantite());
        mouvement.setQteRestant(payload.getQuantite()); // Initialise pour FIFO/LIFO
        mouvement.setPrixUnitaire(payload.getPrixUnitaire());
        mouvement.setDateMouvement(Date.valueOf(payload.getDateMouvement()));

        MouvementStock mouvementSauvegarde = mouvementStockRepository.save(mouvement);

        // 2. Traitement des paiements associes
        List<Caisse> caissesListe = caisseRepository.findAll();

        for (MouvementPaiementPayload payPayload : payload.getPayments()) {
            MouvementStockPaiement paiement = new MouvementStockPaiement();
            paiement.setMouvementStock(mouvementSauvegarde);
            paiement.setCaisse(caisseRepository.getReferenceById(payPayload.getCaisseId()));
            paiement.setMontant(payPayload.getMontant());

            // Verification du solde de la caisse
            for (Caisse caisse : caissesListe) {
                if (caisse.getId().equals(paiement.getCaisse().getId())) {
                    if (caisse.getMontant_actuelle() < paiement.getMontant()) {
                        throw new RuntimeException("Solde insuffisant dans la caisse : " + caisse.getLibelle());
                    }
                    break;
                }
            }

            // Enregistrement de la sortie de caisse
            MouvementCaisse mvtCaisse = new MouvementCaisse();
            mvtCaisse.setCaisse(paiement.getCaisse());
            mvtCaisse.setMontant(-1 * paiement.getMontant());
            mvtCaisse.setDate(mouvementSauvegarde.getDateMouvement());
            mouvementCaisseService.save(mvtCaisse);

            // Mise a jour du solde reel de la caisse
            Caisse caisseToUpdate = paiement.getCaisse();
            caisseToUpdate.setMontant_actuelle(caisseToUpdate.getMontant_actuelle() - paiement.getMontant());
            caisseRepository.save(caisseToUpdate);

            // Sauvegarde du lien paiement-mouvement
            mvtStockPaiementRepository.save(paiement);
        }
    }

    private void enregistrerSortieEtMettreAJourEntrees(MouvementStockPayload payload) {
        Long materielId = payload.getMaterielId();
        Double quantiteASortir = payload.getQuantite();

        // Verification du stock global disponible
        Double totalRestant = mouvementStockRepository.getQuantiteRestantByIdMateriel(materielId);
        if (totalRestant == null || totalRestant < quantiteASortir) {
            double dispo = (totalRestant != null) ? totalRestant : 0.0;
            throw new RuntimeException("Quantite insuffisante. Disponible: " + dispo + ", Demande: " + quantiteASortir);
        }

        // 1. Sauvegarde du mouvement de sortie direct
        MouvementStock sortie = new MouvementStock();
        sortie.setMateriel(materielRepository.getReferenceById(materielId));
        sortie.setTypeMouvement("SORTIE");
        sortie.setQuantite(quantiteASortir);
        sortie.setQteRestant(0.0); // Pas de reste sur une sortie
        sortie.setPrixUnitaire(null);
        sortie.setDateMouvement(Date.valueOf(payload.getDateMouvement()));
        mouvementStockRepository.save(sortie);

        // 2. Mise a jour des lignes d'entree selon la regle FIFO ou LIFO
        List<MouvementStock> entrees = mouvementStockRepository.findAllEntreesDisponiblesByIdMateriel(materielId);
        trierSelonFIFOouLIFO(entrees, entrees.get(0).getMateriel().getTypeGestion());

        for (MouvementStock entree : entrees) {
            if (quantiteASortir <= 0)
                break;

            double mvtQttRestant = entree.getQteRestant();
            if (mvtQttRestant >= quantiteASortir) {
                entree.setQteRestant(mvtQttRestant - quantiteASortir);
                quantiteASortir = 0.0;
            } else {
                quantiteASortir -= mvtQttRestant;
                entree.setQteRestant(0.0);
            }
            mouvementStockRepository.save(entree);
        }
    }

    private void trierSelonFIFOouLIFO(List<MouvementStock> mouvements, String typeGestion) {
        if ("FIFO".equalsIgnoreCase(typeGestion)) {
            mouvements.sort((m1, m2) -> m1.getDateMouvement().compareTo(m2.getDateMouvement()));
        } else if ("LIFO".equalsIgnoreCase(typeGestion)) {
            mouvements.sort((m1, m2) -> m2.getDateMouvement().compareTo(m1.getDateMouvement()));
        }
    }
}