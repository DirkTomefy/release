package mg.bovit.release.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mg.bovit.release.repository.MouvementStockRepository;

@Service
public class MouvementStockService {
    @Autowired
    private MouvementStockRepository mouvementStockRepository;
}
