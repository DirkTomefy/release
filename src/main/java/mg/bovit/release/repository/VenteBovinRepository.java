package mg.bovit.release.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.time.LocalDate;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import mg.bovit.release.model.VenteBovin;

public interface VenteBovinRepository extends JpaRepository<VenteBovin, Long>, JpaSpecificationExecutor<VenteBovin> {
	List<VenteBovin> findByClient_Id(Long clientId);
 @Query(value = "SELECT COUNT(DISTINCT v.id) FROM vente_bovin v " +
       "JOIN vente_detail d ON d.id_vente = v.id " +
       "JOIN bovin b ON b.id = d.id_bovin " +
       "WHERE (CAST(:dateDebut AS DATE) IS NULL OR CAST(v.date_vente AS DATE) >= CAST(:dateDebut AS DATE)) " +
       "AND (CAST(:dateFin AS DATE) IS NULL OR CAST(v.date_vente AS DATE) <= CAST(:dateFin AS DATE)) " +
       "AND (:raceId IS NULL OR b.id_race = :raceId)",
       nativeQuery = true)
 Long countVentesWithFilters(@Param("dateDebut") LocalDate dateDebut,
                            @Param("dateFin") LocalDate dateFin,
                            @Param("raceId") Long raceId);

 @Query(value = "SELECT v.id, v.date_vente, c.nom, c.prenom, COALESCE(SUM(b.prix_vente), 0), COUNT(d.id), f.code_facture, f.id " +
       "FROM vente_bovin v " +
       "JOIN client c ON c.id = v.id_client " +
       "JOIN vente_detail d ON d.id_vente = v.id " +
       "JOIN bovin b ON b.id = d.id_bovin " +
       "LEFT JOIN facture f ON f.id_vente = v.id " +
       "WHERE (:dateDebut IS NULL OR CAST(v.date_vente AS DATE) >= :dateDebut) " +
       "AND (:dateFin IS NULL OR CAST(v.date_vente AS DATE) <= :dateFin) " +
       "AND (:clientId IS NULL OR v.id_client = :clientId) " +
       "AND (:raceId IS NULL OR b.id_race = :raceId) " +
       "GROUP BY v.id, v.date_vente, c.nom, c.prenom, f.code_facture, f.id",
       countQuery = "SELECT COUNT(DISTINCT v.id) FROM vente_bovin v " +
       "JOIN vente_detail d ON d.id_vente = v.id " +
       "JOIN bovin b ON b.id = d.id_bovin " +
       "WHERE (:dateDebut IS NULL OR CAST(v.date_vente AS DATE) >= :dateDebut) " +
       "AND (:dateFin IS NULL OR CAST(v.date_vente AS DATE) <= :dateFin) " +
       "AND (:clientId IS NULL OR v.id_client = :clientId) " +
       "AND (:raceId IS NULL OR b.id_race = :raceId)",
       nativeQuery = true)
 Page<Object[]> searchVentePage(@Param("dateDebut") LocalDate dateDebut,
                               @Param("dateFin") LocalDate dateFin,
                               @Param("clientId") Long clientId,
                               @Param("raceId") Long raceId,
                               Pageable pageable);

 @Query(value = "SELECT v.id, CAST(v.date_vente AS DATE), SUM(b.prix_vente), COUNT(b.id), " +
       "COALESCE(SUM(b.prix_vente - b.prix_achat), 0) " +
       "FROM vente_bovin v " +
       "JOIN vente_detail d ON d.id_vente = v.id " +
       "JOIN bovin b ON b.id = d.id_bovin " +
       "WHERE (CAST(:dateDebut AS DATE) IS NULL OR CAST(v.date_vente AS DATE) >= CAST(:dateDebut AS DATE)) " +
       "AND (CAST(:dateFin AS DATE) IS NULL OR CAST(v.date_vente AS DATE) <= CAST(:dateFin AS DATE)) " +
       "AND (:raceId IS NULL OR b.id_race = :raceId) " +
       "GROUP BY v.id, v.date_vente " +
       "ORDER BY v.date_vente, v.id",
       nativeQuery = true)
 List<Object[]> findVenteStatsGroupedByVente(@Param("dateDebut") LocalDate dateDebut,
                                             @Param("dateFin") LocalDate dateFin,
                                             @Param("raceId") Long raceId);
}
