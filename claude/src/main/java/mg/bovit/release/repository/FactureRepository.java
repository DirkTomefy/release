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

    Optional<Facture> findByCodeFacture(String codeFacture);

    Optional<Facture> findByVente(VenteBovin vente);

    Optional<Facture> findByVente_Id(Long idVente);

    boolean existsByVente_Id(Long idVente);

    // Correction pour PostgreSQL - Utilisation de EXTRACT
    @Query(value = "SELECT COUNT(*) FROM facture f WHERE EXTRACT(MONTH FROM f.date_facture) = :month AND EXTRACT(YEAR FROM f.date_facture) = :year", 
           nativeQuery = true)
    Long countByMonthAndYear(@Param("month") int month, @Param("year") int year);

    // Alternative avec JPQL et FUNCTION (si vous préférez)
    // @Query("SELECT COUNT(f) FROM Facture f WHERE FUNCTION('EXTRACT', 'MONTH', f.dateFacture) = :month AND FUNCTION('EXTRACT', 'YEAR', f.dateFacture) = :year")
    // Long countByMonthAndYear(@Param("month") int month, @Param("year") int year);
}