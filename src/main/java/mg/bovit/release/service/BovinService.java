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

    // function to buy bovin
    @Transactional
    public void buyBovin(Bovin bovin, List<Caisse> caisses, int quantite) {
        // vérify if quantite
        if (quantite <= 0) {
            throw new Exception("la quantite ne doit pas être inférieure ou égal à 0");
        }

        // rectify caisse and verify if enough
        for (int i = 0; i < caisses.size(); i++) {
            if (caisses.get(i))
        }
    }

    public List<Bovin> findAll() {
        return bovinRepository.findAll();
    }
}