package mg.bovit.release.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mg.bovit.release.model.*;

@Repository
public interface VenteBovinRepository extends JpaRepository<VenteBovin, Long>, JpaSpecificationExecutor<VenteBovin> {
 @Query(value = "SELECT COUNT(*) FROM vente_bovin v " +
       "WHERE (CAST(:dateDebut AS DATE) IS NULL OR v.date_vente >= CAST(:dateDebut AS DATE)) " +
       "AND (CAST(:dateFin AS DATE) IS NULL OR v.date_vente <= CAST(:dateFin AS DATE))",
       nativeQuery = true)
Long countVentesWithFilters(@Param("dateDebut") LocalDate dateDebut,
                            @Param("dateFin") LocalDate dateFin);
}
