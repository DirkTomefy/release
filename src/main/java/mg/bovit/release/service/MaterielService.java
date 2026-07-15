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

    @Autowired   // Injection du service pour utiliser les nouvelles méthodes de gestion de stock
    private MouvementStockService mouvementStockService;

    public List<Materiel> findAll() {
        return materielRepository.findAll();
    }

    public List<Materiel> findMaterielByTypeId(Long typeId) {
        return materielRepository.findAll().stream()
                .filter(materiel -> materiel.getType() != null && materiel.getType().getId().equals(typeId))
                .toList();
    }

    // Délègue au service pour une liste de tous les matériels avec leur stock
    public List<MaterielStockDto> findAllMaterielStockRestant() {
        return mouvementStockService.findAllMaterielStockRestant();
    }

    // Délègue au service pour un matériel spécifique
    public MaterielStockDto findMaterielStockRestantById(Long materielId) {
        return mouvementStockService.findMaterielStockRestantById(materielId);
    }

    // Délègue au service pour les matériels d'un type donné
    public List<MaterielStockDto> findByTypeIdWithStock(Long typeId) {
        return mouvementStockService.findMaterielStockRestantByTypeId(typeId);
    }

    // Détails des mouvements (entrées restantes) pour un matériel
    public List<MouvementStock> findDetailsMaterielById(Long id) {
        return mouvementStockRepository.findAllEntreesDisponiblesByIdMateriel(id);
    }
}