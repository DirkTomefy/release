package mg.bovit.release.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import mg.bovit.release.repository.*;
import mg.bovit.release.model.*;

@Service
public class BovinService {
    @Autowired
    private BovinRepository bovinRepository;
    @Autowired
    private CaisseService caisseService;

    // function to buy bovin
    @Transactional
    public void buyBovin(Bovin bovin, List<Caisse> caisses, int quantite) throws Exception {
        // vérify if quantite
        if (quantite <= 0) {
            throw new Exception("la quantite ne doit pas être inférieure ou égal à 0");
        }

        // prix total
        Double prix_total = 0.0;

        // rectify caisse and verify if enough
        for (int i = 0; i < caisses.size(); i++) {
            if (caisses.get(i).getMontant_actuelle() <= 0) {
                throw new Exception("le prix total ne doit pas être inférieure ou égal à 0");
            }
            else {
                // find caisse 
                Caisse temp_caisse = caisseService.findById(caisses.get(i).getId());
                // rectify temp_caisse
                temp_caisse.setMontant_actuelle(temp_caisse.getMontant_actuelle() - caisses.get(i).getMontant_actuelle());
                // save in base
                caisseService.save(temp_caisse);
                
                prix_total = prix_total + caisses.get(i).getMontant_actuelle();
            }
        }

        // calculate prix unitaire
        Double prix_unitaire = prix_total / quantite;
        
        // insert bovin in base
        bovin.setPrix_achat(prix_unitaire);
        for (int i = 0; i < quantite; i++) {
            bovinRepository.save(bovin);
        }
    }

    
    public List<Bovin> findAll() {
        return bovinRepository.findAll();
    }
}