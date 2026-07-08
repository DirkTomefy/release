package mg.bovit.release.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mg.bovit.release.dto.MaterielStockDto;
import mg.bovit.release.dto.MouvementEntreePaiementPayload;
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

    public void faireInventaire(MaterielStockDto materielStockDto,Double quantiteReelle, String dateInventaire) {
        // preparer payload
        if (quantiteReelle > materielStockDto.getQuantiteRestant()) {
            // Si la quantité réelle est supérieure à la quantité restante, c'est une entrée
            MouvementEntreePayload payload = new MouvementEntreePayload();
            payload.setMaterielId(materielStockDto.getMateriel().getId());
            payload.setPrixUnitaire(0.0);
            payload.setQuantite(quantiteReelle - materielStockDto.getQuantiteRestant());
            payload.setDateMouvement(dateInventaire);
            List<MouvementEntreePaiementPayload> payments = new ArrayList<>();
            payments.add(new MouvementEntreePaiementPayload((long) 1, 0.0));
            payload.setPayments(payments);
            entree(payload);
        } else if (quantiteReelle < materielStockDto.getQuantiteRestant()) {
            // Si la quantité réelle est inférieure à la quantité restante, c'est une sortie
            MouvementStockSortiePayload payload = new MouvementStockSortiePayload();
            payload.setMaterielId(materielStockDto.getMateriel().getId());
            payload.setQuantite(materielStockDto.getQuantiteRestant() - quantiteReelle);
            payload.setDateMouvement(dateInventaire);
            // Appeler le service pour enregistrer la sortie
            sortie(payload);
        }
    }
}
