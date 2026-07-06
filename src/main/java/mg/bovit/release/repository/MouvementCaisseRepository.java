package mg.bovit.release.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.bovit.release.model.MouvementCaisse;

@Repository
public interface MouvementCaisseRepository extends JpaRepository<MouvementCaisse, Long> {
    // @Query("SELECT new mg.bovit.release.dto.MouvementCaisseSoldeDto(c, " +
    //         "SUM(CASE WHEN mc.typeMouvement = 'ENTREE' THEN mc.montant ELSE 0 END) - " +
    //         "SUM(CASE WHEN mc.typeMouvement = 'SORTIE' THEN mc.montant ELSE 0 END)) " +
    //         "FROM MouvementCaisse mc " +
    //         "JOIN mc.caisse c " +
    //         "GROUP BY c")
    // List<MouvementCaisseSoldeDto> getAllSoldeByCaisse();
}