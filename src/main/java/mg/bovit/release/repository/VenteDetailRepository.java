package mg.bovit.release.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.bovit.release.model.*;

@Repository
public interface VenteDetailRepository extends JpaRepository<VenteDetail, Long> {

    // Utile plus tard pour afficher le détail d'une vente (liste des bovins vendus)
    List<VenteDetail> findByVenteBovin_Id(Long idVente);
}
