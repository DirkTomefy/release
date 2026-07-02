package mg.bovit.release.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.bovit.release.model.MouvementCaisse;

@Repository
public interface MouvementCaisseRepository extends JpaRepository<MouvementCaisse, Long> {
    
}
