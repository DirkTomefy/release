package mg.bovit.release.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mg.bovit.release.model.MouvementStockSortie;
import mg.bovit.release.repository.MouvementStockSortieRepository;

@Service
public class MouvementStockSortieService {
    @Autowired
    private MouvementStockSortieRepository mouvementStockSortieRepository;

    public void save(MouvementStockSortie mouvementStockSortie) {
        mouvementStockSortieRepository.save(mouvementStockSortie);
    }
}
