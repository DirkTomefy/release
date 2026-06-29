package mg.bovit.release.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

import mg.bovit.release.repository.*;
import mg.bovit.release.model.*;

@Service
public class CaisseService {
    @Autowired
    private CaisseRepository caisseRepository;

    // function to save caisse 
    public Caisse save(Caisse caisse) throws Exception {
        return caisseRepository.save(caisse);
    }

    // function to find caisse by id
    public Caisse findById(Long id_caisse) throws Exception {
        return caisseRepository.findById(id_caisse).orElseThrow();
    }

    public List<Caisse> findAll() {
        return caisseRepository.findAll();
    }
}