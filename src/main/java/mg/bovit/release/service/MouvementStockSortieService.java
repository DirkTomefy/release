package mg.bovit.release.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mg.bovit.release.dto.MouvementStockSortiePayload;
import mg.bovit.release.model.MouvementStockSortie;
import mg.bovit.release.repository.MaterielRepository;
import mg.bovit.release.repository.MouvementStockSortieRepository;

@Service
public class MouvementStockSortieService {
    @Autowired
    private MouvementStockSortieRepository mouvementStockSortieRepository;
    @Autowired
    private MaterielRepository materielRepository;

    public void save(MouvementStockSortie mouvementStockSortie) {
        mouvementStockSortieRepository.save(mouvementStockSortie);
    }

    public MouvementStockSortie saveFromPayloadAndReturn(MouvementStockSortiePayload payload) {
        MouvementStockSortie mouvementStockSortie = new MouvementStockSortie();
        mouvementStockSortie.setMateriel(materielRepository.getReferenceById(payload.getMaterielId()));
        mouvementStockSortie.setQte(payload.getQuantite());
        mouvementStockSortie.setDateSortie(java.sql.Date.valueOf(payload.getDateMouvement()));
        return mouvementStockSortieRepository.save(mouvementStockSortie);
    }
}
