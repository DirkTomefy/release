package mg.bovit.release.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.bovit.release.model.CauseCaisse;

@Repository
public interface CauseCaisseRepository extends JpaRepository<CauseCaisse, Long> {

    // Recherche insensible à la casse, utilisée pour retrouver la cause
    // à appliquer automatiquement à chaque insertion de mvt_caisse
    // (STOCK, ACHAT_BOVIN, ACHAT, PAYEMENT, VENTE, AUTRE...)
    Optional<CauseCaisse> findByLibelleIgnoreCase(String libelle);
    Optional<CauseCaisse> findByLibelle(String libelle);
}
