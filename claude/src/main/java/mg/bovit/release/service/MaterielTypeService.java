package mg.bovit.release.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mg.bovit.release.model.MaterielType;
import mg.bovit.release.repository.MaterielTypeRepository;

@Service
public class MaterielTypeService {
    @Autowired
    private MaterielTypeRepository materielTypeRepository;
    
    public List<MaterielType> findAll() {
        return materielTypeRepository.findAll();
    }
}
