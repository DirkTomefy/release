package mg.bovit.release.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

import mg.bovit.release.repository.*;
import mg.bovit.release.model.*;

@Service
public class RaceService {
    @Autowired
    private RaceRepository raceRepository;

    public List<Race> findAll() {
        return raceRepository.findAll();
    }
}