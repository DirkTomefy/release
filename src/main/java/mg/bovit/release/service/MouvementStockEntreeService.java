package mg.bovit.release.service;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mg.bovit.release.dto.MouvementEntreePayload;
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

    public MouvementStockEntree saveFromPayloadAndReturn(MouvementEntreePayload payload) {
        MouvementStockEntree mouvement = new MouvementStockEntree();
        // on creer une copie de l'objet materiel à partir de l'id du payload
        Materiel materiel = materielRepository.getReferenceById(payload.getMaterielId());
        mouvement.setMateriel(materiel);
        mouvement.setPrixUnitaire(payload.getPrixUnitaire());
        mouvement.setQte(payload.getQuantite());
        mouvement.setQteRestant(payload.getQuantite());
        mouvement.setDateEntree(Date.valueOf(payload.getDateMouvement()));
        return mouvementStockEntreeRepository.save(mouvement); // c'est correct pour sauvegarder l'entité et retourner l'objet sauvegardé ? -> Oui
    }

    @Transactional
    public void transactionerEnregistrerMouvementEntreeEtPaiements(MouvementEntreePayload payload) {
        MouvementStockEntree mouvementStockEntreeSaved = saveFromPayloadAndReturn(payload);
        mouvementStockEntreePaiementService.saveListPaiementFromPayload(payload, mouvementStockEntreeSaved);
    }

    public Double getQuantiteRestantByIdMateriel(Long materielId) {
        return mouvementStockEntreeRepository.getQuantiteRestantByIdMateriel(materielId);
    }
}
