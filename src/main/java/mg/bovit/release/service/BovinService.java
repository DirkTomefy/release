package mg.bovit.release.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

import mg.bovit.release.repository.*;
import mg.bovit.release.model.*;

@Service
public class BovinService {
    @Autowired
    private BovinRepository bovinRepository;

    public List<Bovin> findAll() {
        return bovinRepository.findAll();
    }
}