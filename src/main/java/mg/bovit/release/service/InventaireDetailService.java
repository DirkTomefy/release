package mg.bovit.release.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mg.bovit.release.dto.InventaireDetailPayload;
import mg.bovit.release.dto.InventairePayload;
import mg.bovit.release.model.Inventaire;
import mg.bovit.release.model.InventaireDetail;
import mg.bovit.release.model.Materiel;
import mg.bovit.release.repository.InventaireDetailRepository;
import mg.bovit.release.repository.MaterielRepository;

@Service
public class InventaireDetailService {
    @Autowired
    private InventaireDetailRepository inventaireDetailRepository;
    @Autowired
    private MaterielRepository materielRepository;

    public void saveFromPayload(InventairePayload payload, Inventaire inventaire) {
        for (InventaireDetailPayload inventaireDetailPayload : payload.getDetails()) {
            InventaireDetail inventaireDetail = new InventaireDetail();
            inventaireDetail.setInventaire(inventaire);
            
            Materiel materiel = materielRepository.findById(inventaireDetailPayload.getMaterielId()).orElse(null);
            inventaireDetail.setMateriel(materiel);
            inventaireDetail.setQuantiteInitiale(inventaireDetailPayload.getQuantiteInitiale());
            inventaireDetail.setQuantiteFinale(inventaireDetailPayload.getQuantiteFinale());
            inventaireDetail.setObservations(inventaireDetailPayload.getObservations());
            inventaireDetailRepository.save(inventaireDetail);
        }

    }
}