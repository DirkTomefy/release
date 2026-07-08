package mg.bovit.release.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import mg.bovit.release.dto.RaceCriteria;
import mg.bovit.release.model.Race;
import mg.bovit.release.repository.RaceRepository;
import mg.bovit.release.specification.RaceSpecification;

import java.util.List;

@Service
public class RaceService {

    @Autowired
    private RaceRepository raceRepository;

    // Méthodes existantes
    public Race findById(Long id) throws Exception {
        return raceRepository.findById(id)
                .orElseThrow(() -> new Exception("Race non trouvée avec l'id " + id));
    }

    public List<Race> findAll() {
        return raceRepository.findAll();
    }

    // Nouvelles méthodes
    public Race save(Race race) {
        return raceRepository.save(race);
    }

    public void deleteById(Long id) {
        raceRepository.deleteById(id);
    }

    public Page<Race> findPaginated(RaceCriteria criteria) {
        Specification<Race> spec = RaceSpecification.fromCriteria(criteria);
        Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize());
        return raceRepository.findAll(spec, pageable);
    }
}