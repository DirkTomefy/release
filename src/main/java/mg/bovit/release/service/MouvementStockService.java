package mg.bovit.release.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
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
import mg.bovit.release.model.CauseCaisse;
import mg.bovit.release.model.Materiel;
import mg.bovit.release.model.MaterielType;
import mg.bovit.release.model.MouvementCaisse;
import mg.bovit.release.model.MouvementStock;
import mg.bovit.release.model.MouvementStockPaiement;
import mg.bovit.release.repository.CaisseRepository;
import mg.bovit.release.repository.CauseCaisseRepository;
import mg.bovit.release.repository.MaterielRepository;
import mg.bovit.release.repository.MaterielTypeRepository;
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
    private MaterielTypeRepository materielTypeRepository;

    @Autowired
    private CaisseRepository caisseRepository;

    @Autowired
    private CauseCaisseRepository causeCaisseRepository;

    @Autowired
    private MouvementCaisseService mouvementCaisseService;

    private static final String CAUSE_STOCK = "STOCK";

    // ===================== Méthodes de recherche d'état de stock =====================

    public Map<LocalDate, MaterielStockDto> searchEtatStock(MultiCriteriaEtatStockMateriel form) {
        if (form.getIdMateriel() != null) {
            return searchEtatMaterielStock(form);
        }
        if (form.getIdTypeMateriel() != null) {
            return searchEtatStockTypeMateriel(form);
        }
        return searchEtatStockTotal(form);
    }

    public Map<LocalDate, MaterielStockDto> searchEtatStockTotal(MultiCriteriaEtatStockMateriel form) {
        HashMap<LocalDate, MaterielStockDto> materielStockOnDates = new HashMap<>();
        MaterielStockDto matStockRestant = this.findAllQuantiteRestant(form.getDateDebut());
        List<MouvementStock> mouvementStocks = searchMouvementStock(form);

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

    public Map<LocalDate, MaterielStockDto> searchEtatStockTypeMateriel(MultiCriteriaEtatStockMateriel form) {
        HashMap<LocalDate, MaterielStockDto> materielStockOnDates = new HashMap<>();
        MaterielStockDto matStockRestant = this.findTypeMaterielStockRestant(form.getDateDebut(),
                form.getIdTypeMateriel().longValue());
        List<MouvementStock> mouvementStocks = searchMouvementStock(form);

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

    public Map<LocalDate, MaterielStockDto> searchEtatMaterielStock(MultiCriteriaEtatStockMateriel form) {
        HashMap<LocalDate, MaterielStockDto> materielStockOnDates = new HashMap<>();
        MaterielStockDto matStockRestant = this.findMaterielStockRestant(form.getDateDebut(),
                form.getIdMateriel().longValue());
        List<MouvementStock> mouvementStocks = searchMouvementStock(form);

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

    // ===================== Méthodes de calcul du stock restant =====================

    // Stock total (tous matériels confondus) à une date donnée
    public MaterielStockDto findAllQuantiteRestant(LocalDate date) {
        double entree = mouvementStockRepository.findSommeEntreeToDate(Date.valueOf(date));
        double sortie = mouvementStockRepository.findSommeSortieToDate(Date.valueOf(date));
        double reste = entree - sortie;
        Materiel materiel = new Materiel();
        materiel.setLibelle("All");
        return new MaterielStockDto(materiel, reste);
    }

    // Stock pour un type de matériel à une date donnée
    public MaterielStockDto findTypeMaterielStockRestant(LocalDate date, Long idTypeMateriel) {
        if (!materielTypeRepository.existsById(idTypeMateriel)) {
            throw new RuntimeException("Le type de materiel avec id : " + idTypeMateriel + " est introuvable.");
        }
        double entree = mouvementStockRepository.findSommeEntreeTypeMaterielToDate(idTypeMateriel, Date.valueOf(date));
        double sortie = mouvementStockRepository.findSommeSortieTypeMaterielToDate(idTypeMateriel, Date.valueOf(date));
        double reste = entree - sortie;
        Materiel materiel = new Materiel();
        materiel.setType(materielTypeRepository.findById(idTypeMateriel).get());
        return new MaterielStockDto(materiel, reste);
    }

    // Stock d'un matériel spécifique à une date donnée
    public MaterielStockDto findMaterielStockRestant(LocalDate date, Long idMateriel) {
        if (!materielRepository.existsById(idMateriel)) {
            throw new RuntimeException("Le materiel avec id : " + idMateriel + " est introuvable.");
        }
        double entree = mouvementStockRepository.findSommeEntreeMaterielToDate(idMateriel, Date.valueOf(date));
        double sortie = mouvementStockRepository.findSommeSortieMaterielToDate(idMateriel, Date.valueOf(date));
        double reste = entree - sortie;
        return new MaterielStockDto(materielRepository.findById(idMateriel).get(), reste);
    }

    // ===================== Nouvelles méthodes pour les listes de stock =====================

    // Liste de tous les matériels avec leur stock actuel (dernier qteRestant)
    public List<MaterielStockDto> findAllMaterielStockRestant() {
        List<Object[]> rows = mouvementStockRepository.findAllMaterielWithLastStock();
        List<MaterielStockDto> result = new ArrayList<>();
        for (Object[] row : rows) {
            Long id = ((Number) row[0]).longValue();
            String libelle = (String) row[1];
            Long typeId = ((Number) row[2]).longValue();
            String typeLibelle = (String) row[3];
            Double qteRestant = ((Number) row[4]).doubleValue();

            MaterielType type = new MaterielType();
            type.setId(typeId);
            type.setLibelle(typeLibelle);

            Materiel materiel = new Materiel();
            materiel.setId(id);
            materiel.setLibelle(libelle);
            materiel.setType(type);

            result.add(new MaterielStockDto(materiel, qteRestant));
        }
        return result;
    }

    // Stock actuel d'un matériel spécifique (dernier qteRestant ou calcul entrées - sorties)
    public MaterielStockDto findMaterielStockRestantById(Long materielId) {
        Materiel materiel = materielRepository.findById(materielId)
                .orElseThrow(() -> new RuntimeException("Matériel introuvable"));
        double stock = mouvementStockRepository.getStockActuelByMaterielId(materielId);
        return new MaterielStockDto(materiel, stock);
    }

    // Liste des matériels d'un type donné avec leur stock actuel
    public List<MaterielStockDto> findMaterielStockRestantByTypeId(Long typeId) {
        List<Object[]> rows = mouvementStockRepository.findMaterielStockRestantByTypeIdNative(typeId);
        List<MaterielStockDto> result = new ArrayList<>();
        for (Object[] row : rows) {
            Long id = ((Number) row[0]).longValue();
            String libelle = (String) row[1];
            Long typeId2 = ((Number) row[2]).longValue();
            String typeLibelle = (String) row[3];
            Double qteRestant = ((Number) row[4]).doubleValue();

            MaterielType type = new MaterielType();
            type.setId(typeId2);
            type.setLibelle(typeLibelle);

            Materiel materiel = new Materiel();
            materiel.setId(id);
            materiel.setLibelle(libelle);
            materiel.setType(type);

            result.add(new MaterielStockDto(materiel, qteRestant));
        }
        return result;
    }

    // ===================== Méthodes CRUD de base =====================

    @Transactional(readOnly = true)
    public List<MouvementStock> findAll() {
        return mouvementStockRepository.findAll(Sort.by(Sort.Direction.DESC, "dateMouvement"));
    }

    @Transactional(readOnly = true)
    public MouvementStock findById(Long id) {
        return mouvementStockRepository.findById(id).orElse(null);
    }

    // ===================== Traitement des mouvements (entrée / sortie) =====================

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
        MouvementStock mouvement = new MouvementStock();
        Materiel materiel = materielRepository.getReferenceById(payload.getMaterielId());

        mouvement.setMateriel(materiel);
        mouvement.setTypeMouvement("ENTREE");
        mouvement.setQuantite(payload.getQuantite());
        mouvement.setQteRestant(payload.getQuantite()); // initialiser 
        mouvement.setPrixUnitaire(payload.getPrixUnitaire());
        mouvement.setDateMouvement(Date.valueOf(payload.getDateMouvement()));

        MouvementStock mouvementSauvegarde = mouvementStockRepository.save(mouvement);

        List<Caisse> caissesListe = caisseRepository.findAll();

        for (MouvementPaiementPayload payPayload : payload.getPayments()) {
            MouvementStockPaiement paiement = new MouvementStockPaiement();
            paiement.setMouvementStock(mouvementSauvegarde);
            paiement.setCaisse(caisseRepository.getReferenceById(payPayload.getCaisseId()));
            paiement.setMontant(payPayload.getMontant());

            for (Caisse caisse : caissesListe) {
                if (caisse.getId().equals(paiement.getCaisse().getId())) {
                    if (caisse.getMontant_actuelle() < paiement.getMontant()) {
                        throw new RuntimeException("Solde insuffisant dans la caisse : " + caisse.getLibelle());
                    }
                    break;
                }
            }

            MouvementCaisse mvtCaisse = new MouvementCaisse();
            mvtCaisse.setCaisse(paiement.getCaisse());
            mvtCaisse.setMontant(-1 * paiement.getMontant());
            mvtCaisse.setDate(mouvementSauvegarde.getDateMouvement());
            mvtCaisse.setCauseCaisse(getCauseCaisse(CAUSE_STOCK));
            mouvementCaisseService.save(mvtCaisse);

            Caisse caisseToUpdate = paiement.getCaisse();
            caisseToUpdate.setMontant_actuelle(caisseToUpdate.getMontant_actuelle() - paiement.getMontant());
            caisseRepository.save(caisseToUpdate);

            mvtStockPaiementRepository.save(paiement);
        }
    }

    private void enregistrerSortieEtMettreAJourEntrees(MouvementStockPayload payload) {
        Long materielId = payload.getMaterielId();
        Double quantiteASortir = payload.getQuantite();

        // Vérification du stock disponible via la nouvelle méthode
        Double totalRestant = mouvementStockRepository.getStockActuelByMaterielId(materielId);
        if (totalRestant < quantiteASortir) {
            throw new RuntimeException("Quantité insuffisante. Disponible: " + totalRestant + ", Demandée: " + quantiteASortir);
        }

        // Sauvegarde du mouvement de sortie
        MouvementStock sortie = new MouvementStock();
        sortie.setMateriel(materielRepository.getReferenceById(materielId));
        sortie.setTypeMouvement("SORTIE");
        sortie.setQuantite(quantiteASortir);
        sortie.setQteRestant(0.0);
        sortie.setPrixUnitaire(null);
        sortie.setDateMouvement(Date.valueOf(payload.getDateMouvement()));
        mouvementStockRepository.save(sortie);

        // Mise à jour des lignes d'entrée selon FIFO ou LIFO
        List<MouvementStock> entrees = mouvementStockRepository.findAllEntreesDisponiblesByIdMateriel(materielId);
        if (!entrees.isEmpty()) {
            trierSelonFIFOouLIFO(entrees, entrees.get(0).getMateriel().getTypeGestion());
        }

        for (MouvementStock entree : entrees) {
            if (quantiteASortir <= 0) break;

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

    private CauseCaisse getCauseCaisse(String libelle) {
        return causeCaisseRepository.findByLibelleIgnoreCase(libelle)
                .orElseThrow(() -> new RuntimeException("Cause de caisse introuvable : " + libelle));
    }
}