package mg.bovit.release.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mg.bovit.release.dto.MaterielStockDto;
import mg.bovit.release.model.Materiel;
import mg.bovit.release.repository.MaterielRepository;
import mg.bovit.release.repository.MouvementStockEntreeRepository;

@Service
public class MaterielService {
    @Autowired
    private MaterielRepository materielRepository;
    @Autowired
    private MouvementStockEntreeRepository mouvementStockEntreeRepository;
    
    public List<Materiel> findAll() {
        return materielRepository.findAll();
    }

    public List<Materiel> findMaterielByTypeId(Long typeId) {
        return materielRepository.findAll().stream()
                .filter(materiel -> materiel.getType() != null && materiel.getType().getId().equals(typeId))
                .toList();
    }

    public List<MaterielStockDto> findAllMaterielStockRestant() {
        return mouvementStockEntreeRepository.findAllMaterielStockRestant();
    }
}
