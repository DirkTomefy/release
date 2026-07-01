package mg.bovit.release.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.bovit.release.model.MvtCaisse;

@Repository
public interface MvtCaisseRepository extends JpaRepository<MvtCaisse, Long> {
    
}