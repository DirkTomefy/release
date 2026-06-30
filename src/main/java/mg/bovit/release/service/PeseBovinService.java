package mg.bovit.release.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;
import mg.bovit.release.specification.BovinSpecification;
import mg.bovit.release.repository.*;
import mg.bovit.release.model.*;
import mg.bovit.release.dto.MultiCriteriaFormBovinList;

@Service
public class PeseBovinService {
    @Autowired 
    PeseBovinRepository peseBovinRepository;

    // function to get bovin

    // function to save pese_bovin
    public PeseBovin save(PeseBovin peseBovin) {
        return peseBovinRepository.save(peseBovin);
    }
    
    // function to findAll pese_bovin
    public List<PeseBovin> findAll() {
        return peseBovinRepository.findAll();
    }
}
