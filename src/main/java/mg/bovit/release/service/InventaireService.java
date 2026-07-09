package mg.bovit.release.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import mg.bovit.release.dto.MaterielStockDto;
import mg.bovit.release.dto.MouvementPaiementPayload;
import mg.bovit.release.dto.MouvementStockPayload;

@Service
public class InventaireService {
    @Autowired
    private MouvementStockService mouvementStockService;

    public void faireInventaire(MaterielStockDto materielStockDto, Double quantiteReelle, String dateInventaire) {
        double quantiteRestanteFormate = (materielStockDto.getQuantiteRestant() != null) ? materielStockDto.getQuantiteRestant() : 0.0;

        if (quantiteReelle > quantiteRestanteFormate) {
            // Si la quantite reelle est superieure, on cree une ENTREE de regularisation a prix 0
            MouvementStockPayload payload = new MouvementStockPayload();
            payload.setTypeMouvement("ENTREE");
            payload.setMaterielId(materielStockDto.getMateriel().getId());
            payload.setQuantite(quantiteReelle - quantiteRestanteFormate);
            payload.setDateMouvement(dateInventaire);
            payload.setPrixUnitaire(0.0);
            
            List<MouvementPaiementPayload> payments = new ArrayList<>();
            payments.add(new MouvementPaiementPayload((long) 1, 0.0));
            payload.setPayments(payments);
            
            mouvementStockService.traiterMouvementStock(payload);

        } else if (quantiteReelle < quantiteRestanteFormate) {
            // Si la quantite reelle est inferieure, on cree une SORTIE de regularisation
            MouvementStockPayload payload = new MouvementStockPayload();
            payload.setTypeMouvement("SORTIE");
            payload.setMaterielId(materielStockDto.getMateriel().getId());
            payload.setQuantite(quantiteRestanteFormate - quantiteReelle);
            payload.setDateMouvement(dateInventaire);
            payload.setPrixUnitaire(null);
            payload.setPayments(new ArrayList<>());
            
            mouvementStockService.traiterMouvementStock(payload);
        }
    }
}