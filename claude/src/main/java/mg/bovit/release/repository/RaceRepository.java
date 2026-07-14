package mg.bovit.release.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import mg.bovit.release.model.Race;

@Repository
public interface RaceRepository extends JpaRepository<Race, Long>, JpaSpecificationExecutor<Race> {
    Optional<Race> findByNom(String nom);
    List<Race> findAllByNom(String nom);
    // aucune méthode supplémentaire nécessaire
}