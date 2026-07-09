package mg.bovit.release.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import mg.bovit.release.dto.MaterielStockDto;
import mg.bovit.release.dto.MouvementPaiementPayload;
import mg.bovit.release.dto.MouvementStockPayload;
import mg.bovit.release.model.Inventaire;
import mg.bovit.release.model.InventaireDetail;
import mg.bovit.release.repository.InventaireDetailRepository;
import mg.bovit.release.repository.InventaireRepository;

@Service
public class InventaireService {
    @Autowired
    private MouvementStockService mouvementStockService;
    @Autowired
    private InventaireRepository inventaireRepository;
    @Autowired
    private InventaireDetailRepository inventaireDetailRepository;

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

    public List<Inventaire> listerInventaires() {
        return inventaireRepository.findAll();
    }

    public List<InventaireDetail> listerInventairesDetails() {
        return inventaireDetailRepository.findAll();
    }
}