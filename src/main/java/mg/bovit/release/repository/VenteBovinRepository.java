package mg.bovit.release.repository;

import java.time.LocalDate;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

 @Query(value = "SELECT v.id, v.date_vente, c.nom, c.prenom, SUM(b.prix_vente), COUNT(d.id), f.code_facture, f.id " +
       "FROM vente_bovin v " +
       "JOIN client c ON c.id = v.id_client " +
       "JOIN vente_detail d ON d.id_vente = v.id " +
       "JOIN bovin b ON b.id = d.id_bovin " +
       "LEFT JOIN facture f ON f.id_vente = v.id " +
       "WHERE (:dateDebut IS NULL OR v.date_vente >= :dateDebut) " +
       "AND (:dateFin IS NULL OR v.date_vente <= :dateFin) " +
       "AND (:clientId IS NULL OR v.id_client = :clientId) " +
       "AND (:raceId IS NULL OR b.id_race = :raceId) " +
       "GROUP BY v.id, v.date_vente, c.nom, c.prenom, f.code_facture, f.id",
       countQuery = "SELECT COUNT(DISTINCT v.id) FROM vente_bovin v " +
       "JOIN vente_detail d ON d.id_vente = v.id " +
       "JOIN bovin b ON b.id = d.id_bovin " +
       "WHERE (:dateDebut IS NULL OR v.date_vente >= :dateDebut) " +
       "AND (:dateFin IS NULL OR v.date_vente <= :dateFin) " +
       "AND (:clientId IS NULL OR v.id_client = :clientId) " +
       "AND (:raceId IS NULL OR b.id_race = :raceId)",
       nativeQuery = true)
 Page<Object[]> searchVentePage(@Param("dateDebut") LocalDate dateDebut,
                               @Param("dateFin") LocalDate dateFin,
                               @Param("clientId") Long clientId,
                               @Param("raceId") Long raceId,
                               Pageable pageable);
}
