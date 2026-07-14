package mg.bovit.release.repository;

import mg.bovit.release.model.FactureDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FactureDetailRepository extends JpaRepository<FactureDetail, Long> {

    /**
     * Récupère tous les détails d'une facture donnée (par son ID)
     */
    List<FactureDetail> findByFacture_Id(Long factureId);

    /**
     * Récupère un détail de facture à partir du détail de vente associé
     * (utile pour vérifier si un bovin a déjà été facturé)
     */
    Optional<FactureDetail> findByVenteDetail_Id(Long venteDetailId);

    /**
     * Supprime tous les détails d'une facture (utilisé en cascade normalement)
     */
    void deleteByFacture_Id(Long factureId);

    /**
     * Compte le nombre de lignes dans une facture
     */
    long countByFacture_Id(Long factureId);

    /**
     * Récupère les détails d'une facture avec les données associées (vente_detail, bovin, race)
     * pour éviter les problèmes de N+1 lors de l'affichage
     */
    @Query("SELECT fd FROM FactureDetail fd " +
           "JOIN FETCH fd.venteDetail vd " +
           "JOIN FETCH vd.bovin b " +
           "JOIN FETCH b.race " +
           "WHERE fd.facture.id = :factureId")
    List<FactureDetail> findDetailsWithBovinByFactureId(@Param("factureId") Long factureId);
}