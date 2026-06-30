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
public class PesePoidsService {
    @Autowired 
    PesePoidsRepository pesePoidsRepository;
    
    // function to findAll pesePoids
    public List<PesePoids> findAll() {
        return pesePoidsRepository.findAll();
    }
}
