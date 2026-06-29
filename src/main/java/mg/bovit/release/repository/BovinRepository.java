package mg.bovit.release.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import mg.bovit.release.model.*;

@Repository
public interface BovinRepository extends JpaRepository<Bovin, Long>, JpaSpecificationExecutor<Bovin> {
    
}