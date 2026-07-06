package mg.bovit.release.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mg.bovit.release.model.MouvementCaisse;
import mg.bovit.release.repository.MouvementCaisseRepository;

@Service
public class MouvementCaisseService {
    @Autowired
    private MouvementCaisseRepository mouvementCaisseRepository;

    public MouvementCaisse save(MouvementCaisse mouvementCaisse) {
        return mouvementCaisseRepository.save(mouvementCaisse);
    }

    public List<MouvementCaisse> findAll() {
        return mouvementCaisseRepository.findAll();
    }

    // public List<MouvementCaisseSoldeDto> getAllSoldeByCaisse() {
    //     return mouvementCaisseRepository.getAllSoldeByCaisse();
    // }
}
