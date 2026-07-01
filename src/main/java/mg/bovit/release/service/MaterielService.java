package mg.bovit.release.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mg.bovit.release.model.Materiel;
import mg.bovit.release.repository.MaterielRepository;

@Service
public class MaterielService {
    @Autowired
    private MaterielRepository materielRepository;
    
    public List<Materiel> findAll() {
        return materielRepository.findAll();
    }

    public List<Materiel> findMaterielByTypeId(Long typeId) {
        return materielRepository.findAll().stream()
                .filter(materiel -> materiel.getType() != null && materiel.getType().getId().equals(typeId))
                .toList();
    }
}
