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

    // function to find race by id
    public Race findById(Long id_race) throws Exception {
        return raceRepository.findById(id_race).orElseThrow();
    }

    public List<Race> findAll() {
        return raceRepository.findAll();
    }
}