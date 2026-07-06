package mg.bovit.release.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mg.bovit.release.dto.MouvementEntreePaiementPayload;
import mg.bovit.release.dto.MouvementEntreePayload;
import mg.bovit.release.model.Caisse;
import mg.bovit.release.model.MouvementCaisse;
import mg.bovit.release.model.MouvementStockEntree;
import mg.bovit.release.model.MouvementStockEntreePaiement;
import mg.bovit.release.repository.CaisseRepository;
import mg.bovit.release.repository.MouvementStockEntreePaiementRepository;

@Service
public class MouvementStockEntreePaiementService {
    @Autowired
    private MouvementStockEntreePaiementRepository mouvementStockEntreePaiementRepository;
    @Autowired
    private MouvementCaisseService mouvementCaisseService;
    @Autowired
    private CaisseRepository caisseRepository;

    public void saveListPaiementFromPayload(MouvementEntreePayload payload, MouvementStockEntree mouvementStockEntreeSaved) {
        List<Caisse> caissesListe = caisseRepository.findAll();
        // on extrait les paiements du payload et on les sauvegarde
        for (MouvementEntreePaiementPayload mouvementEntreePaiementPayload : payload.getPayments()) {
            MouvementStockEntreePaiement mouvementPaiement = new MouvementStockEntreePaiement();
            mouvementPaiement.setMouvementStockEntree(mouvementStockEntreeSaved);
            mouvementPaiement.setCaisse(caisseRepository.getReferenceById(mouvementEntreePaiementPayload.getCaisseId()));
            mouvementPaiement.setMontant(mouvementEntreePaiementPayload.getMontant());
            // on cherche la caisse correspondante dans la liste des caisses pour verifier le solde
            for (Caisse caisse : caissesListe) {
                if (caisse.getId().equals(mouvementPaiement.getCaisse().getId())) {
                    if (caisse.getMontant_actuelle() < mouvementPaiement.getMontant()) {
                        throw new RuntimeException("Au moins une caisse n'a pas assez de solde pour effectuer le paiement. Caisse: " + mouvementPaiement.getCaisse().getLibelle() + ", Solde: " + caisse.getMontant_actuelle() + ", Montant du paiement: " + mouvementPaiement.getMontant());
                    }
                    break;
                }
            }
            // save la sortie de caisse
            MouvementCaisse mouvementCaisse = new MouvementCaisse();
            mouvementCaisse.setCaisse(mouvementPaiement.getCaisse());
            // mouvementCaisse.setTypeMouvement("SORTIE");
            mouvementCaisse.setMontant(-1 * mouvementPaiement.getMontant());
            mouvementCaisse.setDate(mouvementStockEntreeSaved.getDateEntree());
            mouvementCaisseService.save(mouvementCaisse);

            // update le solde de la caisse
            Caisse caisseToUpdate = mouvementPaiement.getCaisse();
            caisseToUpdate.setMontant_actuelle(caisseToUpdate.getMontant_actuelle() - mouvementPaiement.getMontant());
            caisseRepository.save(caisseToUpdate);

            // save le paiement du mouvement d'entree
            mouvementStockEntreePaiementRepository.save(mouvementPaiement);
        }
    }
}
