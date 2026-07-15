package mg.bovit.release.repository;

import java.util.Optional;

// import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import mg.bovit.release.model.*;

@Repository
public interface MaterielRepository extends JpaRepository<Materiel, Long>, JpaSpecificationExecutor<Materiel> {
    Optional<Materiel> findByLibelle(String libelle);
}