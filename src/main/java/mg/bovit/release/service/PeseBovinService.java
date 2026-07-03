package mg.bovit.release.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import mg.bovit.release.dto.MulticriteriaListPeseBovin;
import mg.bovit.release.model.PeseBovin;
import mg.bovit.release.repository.PeseBovinRepository;
import mg.bovit.release.specification.PeseSpecification;

@Service
public class PeseBovinService {
    @Autowired 
    PeseBovinRepository peseBovinRepository;

    // function to find peseBovin by id
    public PeseBovin findById(Long id_peseBovin) {
        return peseBovinRepository.findById(id_peseBovin).orElse(null);
    }

    // function to get latest pese by bovin
    public PeseBovin getLatestPeseByBovin(Long id_bovin) {
        return peseBovinRepository.getLatestPeseByBovin(id_bovin);
    }

    // function to save pese_bovin
    public PeseBovin save(PeseBovin peseBovin) {
        return peseBovinRepository.save(peseBovin);
    }
    
    // function to findAll pese_bovin
    public List<PeseBovin> findAll() {
        return peseBovinRepository.findAll();
    }

    public Page<PeseBovin> searchPeseBovins(MulticriteriaListPeseBovin form) {
        // Construction du tri
        String sortField = "id";
        Sort.Direction direction = Sort.Direction.ASC;
        if (form.getSort() != null && !form.getSort().isEmpty()) {
            String[] parts = form.getSort().split(",");
            if (parts.length >= 1) {
                sortField = parts[0];
            }
            if (parts.length >= 2) {
                direction = Sort.Direction.fromString(parts[1]);
            }
        }
        Pageable pageable = PageRequest.of(
                form.getPage(),
                form.getSize(),
                Sort.by(direction, sortField)
        );

        return peseBovinRepository.findAll(PeseSpecification.fromForm(form), pageable);
    }

    // ✅ NOUVELLE MÉTHODE : Récupérer toutes les pesées d'un bovin triées par date
    public List<PeseBovin> findByBovinIdOrderByDatePeseAsc(Long bovinId) {
        return peseBovinRepository.findByBovinIdOrderByDatePeseAsc(bovinId);
    }
}