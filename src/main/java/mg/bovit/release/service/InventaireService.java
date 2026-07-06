package mg.bovit.release.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mg.bovit.release.dto.MouvementEntreePayload;
import mg.bovit.release.dto.MouvementStockSortiePayload;

@Service
public class InventaireService {
    @Autowired
    private MouvementStockEntreeService mouvementStockEntreeService;
    @Autowired
    private MouvementStockSortieService mouvementStockSortieService;

    public void entree(MouvementEntreePayload payload) {
        mouvementStockEntreeService.transactionerEnregistrerMouvementEntreeEtPaiements(payload);
    }

    public void sortie(MouvementStockSortiePayload payload) {
        mouvementStockSortieService.transactionerEnregistrerMouvementSortieEtUpdateMouvementEntree(payload);
    }

    public void faireInventaire() {
        // preparer payload
    }
}
