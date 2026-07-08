package mg.bovit.release.service;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mg.bovit.release.dto.MouvementEntreePayload;
import mg.bovit.release.dto.MouvementStockSortiePayload;
import mg.bovit.release.model.Materiel;
import mg.bovit.release.model.MouvementStockEntree;
import mg.bovit.release.repository.MaterielRepository;
import mg.bovit.release.repository.MouvementStockEntreeRepository;

@Service
public class MouvementStockEntreeService {
    @Autowired
    private MouvementStockEntreePaiementService mouvementStockEntreePaiementService;
    @Autowired
    private MouvementStockEntreeRepository mouvementStockEntreeRepository;
    @Autowired
    private MaterielRepository materielRepository;

    public List<MouvementStockEntree> findAll() {
        return mouvementStockEntreeRepository.findAll();
    }

    public List<MouvementStockEntree> findAllByIdMateriel(Long materielId) {
        return mouvementStockEntreeRepository.findAllByIdMateriel(materielId);
    }

    public MouvementStockEntree saveFromPayloadAndReturn(MouvementEntreePayload payload) {
        MouvementStockEntree mouvement = new MouvementStockEntree();

        // on creer une copie de l'objet materiel à partir de l'id du payload
        Materiel materiel = materielRepository.getReferenceById(payload.getMaterielId());
        mouvement.setMateriel(materiel);
        mouvement.setPrixUnitaire(payload.getPrixUnitaire());
        mouvement.setQte(payload.getQuantite());
        mouvement.setQteRestant(payload.getQuantite());
        mouvement.setDateEntree(Date.valueOf(payload.getDateMouvement()));
        return mouvementStockEntreeRepository.save(mouvement); // sauvegarder l'entite et retourner
    }

    @Transactional
    public void transactionerEnregistrerMouvementEntreeEtPaiements(MouvementEntreePayload payload) {
        MouvementStockEntree mouvementStockEntreeSaved = saveFromPayloadAndReturn(payload);
        mouvementStockEntreePaiementService.saveListPaiementFromPayload(payload, mouvementStockEntreeSaved);
    }

    public Double getQuantiteRestantByIdMateriel(Long materielId) {
        return mouvementStockEntreeRepository.getQuantiteRestantByIdMateriel(materielId);
    }

    public List<MouvementStockEntree> trierSelonFIFOouLIFO(List<MouvementStockEntree> mouvements) {
        Materiel tempMateriel = materielRepository.getReferenceById(mouvements.get(0).getMateriel().getId());
        String fifoOuLifo = tempMateriel.getTypeGestion();
        if (fifoOuLifo.equals("FIFO")) {
            mouvements.sort((m1, m2) -> m1.getDateEntree().compareTo(m2.getDateEntree()));
        } else if (fifoOuLifo.equals("LIFO")) {
            mouvements.sort((m1, m2) -> m2.getDateEntree().compareTo(m1.getDateEntree()));
        }
        return mouvements;
    }

    public void updateApresSortie(MouvementStockSortiePayload payload) {
        double quantiteRestant = getQuantiteRestantByIdMateriel(payload.getMaterielId());
        double quantiteASortir = payload.getQuantite();
        List<MouvementStockEntree> mouvements = findAllByIdMateriel(payload.getMaterielId());
        if (quantiteASortir > quantiteRestant)
            throw new RuntimeException(
                    "La quantité restante du matériel est insuffisante pour effectuer la sortie. Quantité restante: "
                            + getQuantiteRestantByIdMateriel(payload.getMaterielId()) + ", Quantité demandée: "
                            + payload.getQuantite());

        // avant on inverse ou pas la liste des mouvements selon le fifo ou lifo
        trierSelonFIFOouLIFO(mouvements);

        for (MouvementStockEntree mouvementStockEntreeAReduire : mouvements) {
            if (quantiteASortir <= 0)
                break;
            double mvtQttRestant = mouvementStockEntreeAReduire.getQteRestant();
            if (mvtQttRestant >= quantiteASortir) {
                double mvtQttRestantActuelle = mvtQttRestant - quantiteASortir;
                quantiteASortir = 0;
                mouvementStockEntreeAReduire.setQteRestant(mvtQttRestantActuelle);
                mouvementStockEntreeRepository.save(mouvementStockEntreeAReduire);
            } else {
                double mvtQttRestantActuelle = 0;
                quantiteASortir = quantiteASortir - mvtQttRestant;
                mouvementStockEntreeAReduire.setQteRestant(mvtQttRestantActuelle);
                mouvementStockEntreeRepository.save(mouvementStockEntreeAReduire);
            }
        }
    }
}