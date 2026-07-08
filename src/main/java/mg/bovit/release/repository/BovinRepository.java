package mg.bovit.release.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mg.bovit.release.model.*;

@Repository
public interface BovinRepository extends JpaRepository<Bovin, Long>, JpaSpecificationExecutor<Bovin> {
      @Query(value = "SELECT TO_CHAR(DATE_TRUNC('month', b.date_vente), 'YYYY-MM') AS mois, " +
       "SUM(b.prix_vente), COUNT(b) " +
       "FROM bovin b " +
       "WHERE b.date_vente IS NOT NULL " +
       "AND (CAST(:dateDebut AS DATE) IS NULL OR b.date_vente >= CAST(:dateDebut AS DATE)) " +
       "AND (CAST(:dateFin AS DATE) IS NULL OR b.date_vente <= CAST(:dateFin AS DATE)) " +
       "AND (CAST(:raceId AS INTEGER) IS NULL OR b.id_race = CAST(:raceId AS INTEGER)) " +
       "GROUP BY mois " +
       "ORDER BY mois",
       nativeQuery = true)
List<Object[]> findVenteStatsGroupedByMonth(@Param("dateDebut") LocalDate dateDebut,
                                            @Param("dateFin") LocalDate dateFin,
                                            @Param("raceId") Long raceId);
}