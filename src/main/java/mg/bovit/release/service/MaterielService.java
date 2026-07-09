package mg.bovit.release.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import mg.bovit.release.dto.MaterielStockDto;
import mg.bovit.release.model.Materiel;
import mg.bovit.release.model.MouvementStock;
import mg.bovit.release.repository.MaterielRepository;
import mg.bovit.release.repository.MouvementStockRepository;

@Service
public class MaterielService {
    @Autowired
    private MaterielRepository materielRepository;
    @Autowired
    private MouvementStockRepository mouvementStockRepository;
    
    public List<Materiel> findAll() {
        return materielRepository.findAll();
    }

    public List<Materiel> findMaterielByTypeId(Long typeId) {
        return materielRepository.findAll().stream()
                .filter(materiel -> materiel.getType() != null && materiel.getType().getId().equals(typeId))
                .toList();
    }

    public List<MaterielStockDto> findAllMaterielStockRestant() {
        return mouvementStockRepository.findAllMaterielStockRestant();
    }

    public MaterielStockDto findMaterielStockRestantById(Long materielId) {
        return mouvementStockRepository.findMaterielStockRestantById(materielId);
    }

    public List<MouvementStock> findDetailsMaterielById(Long id) {
        return mouvementStockRepository.findAllEntreesDisponiblesByIdMateriel(id);
    }
}