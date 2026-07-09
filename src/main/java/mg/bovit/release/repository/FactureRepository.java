package mg.bovit.release.repository;

import mg.bovit.release.model.Facture;
import mg.bovit.release.model.VenteBovin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Long> {

    // Recherche par code_facture
    Optional<Facture> findByCodeFacture(String codeFacture);

    // Recherche par vente (via l'entité)
    Optional<Facture> findByVente(VenteBovin vente);

    // Alternative : recherche par ID de vente
    Optional<Facture> findByVente_Id(Long idVente);

    // Compte le nombre de factures pour un mois/année donnés
    @Query("SELECT COUNT(f) FROM Facture f WHERE FUNCTION('MONTH', f.dateFacture) = :month AND FUNCTION('YEAR', f.dateFacture) = :year")
    Long countByMonthAndYear(@Param("month") int month, @Param("year") int year);
}