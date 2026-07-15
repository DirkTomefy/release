package mg.bovit.release.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mg.bovit.release.model.Mortalite;

@Repository
public interface MortaliteRepository extends JpaRepository<Mortalite, Long>, JpaSpecificationExecutor<Mortalite> {

    @Query(value = "SELECT COUNT(m.id) FROM mortalite m " +
            "WHERE (CAST(:dateDebut AS DATE) IS NULL OR m.date >= CAST(:dateDebut AS DATE)) " +
            "AND (CAST(:dateFin AS DATE) IS NULL OR m.date <= CAST(:dateFin AS DATE)) " +
            "AND (CAST(:raceId AS INTEGER) IS NULL OR m.id_race = CAST(:raceId AS INTEGER))",
            nativeQuery = true)
    Long countMortalitesWithFilters(@Param("dateDebut") LocalDate dateDebut,
                                     @Param("dateFin") LocalDate dateFin,
                                     @Param("raceId") Long raceId);

    @Query(value = "SELECT COALESCE(SUM(m.prix_achat), 0) FROM mortalite m " +
            "WHERE (CAST(:dateDebut AS DATE) IS NULL OR m.date >= CAST(:dateDebut AS DATE)) " +
            "AND (CAST(:dateFin AS DATE) IS NULL OR m.date <= CAST(:dateFin AS DATE)) " +
            "AND (CAST(:raceId AS INTEGER) IS NULL OR m.id_race = CAST(:raceId AS INTEGER))",
            nativeQuery = true)
    Double sumPrixMortalitesWithFilters(@Param("dateDebut") LocalDate dateDebut,
                                       @Param("dateFin") LocalDate dateFin,
                                       @Param("raceId") Long raceId);

    @Query(value = "SELECT TO_CHAR(DATE_TRUNC('month', m.date), 'YYYY-MM') AS mois, COUNT(m.id), COALESCE(SUM(m.prix_achat), 0) AS prix_total " +
            "FROM mortalite m " +
            "WHERE (CAST(:dateDebut AS DATE) IS NULL OR m.date >= CAST(:dateDebut AS DATE)) " +
            "AND (CAST(:dateFin AS DATE) IS NULL OR m.date <= CAST(:dateFin AS DATE)) " +
            "AND (CAST(:raceId AS INTEGER) IS NULL OR m.id_race = CAST(:raceId AS INTEGER)) " +
            "GROUP BY mois " +
            "ORDER BY mois",
            nativeQuery = true)
    List<Object[]> findMortaliteStatsGroupedByMonth(@Param("dateDebut") LocalDate dateDebut,
                                                     @Param("dateFin") LocalDate dateFin,
                                                     @Param("raceId") Long raceId);
}
