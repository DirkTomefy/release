package mg.bovit.release.service;

import mg.bovit.release.model.VenteDetail;
import mg.bovit.release.repository.VenteDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VenteDetailService {

    @Autowired
    private VenteDetailRepository venteDetailRepository;

    public List<VenteDetail> findByVenteId(Long venteId) {
        return venteDetailRepository.findByVenteBovin_Id(venteId);
    }
}