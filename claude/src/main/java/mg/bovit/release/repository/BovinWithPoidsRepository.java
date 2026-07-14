package mg.bovit.release.repository;

import mg.bovit.release.model.sqlview.BovinWithPoids;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BovinWithPoidsRepository extends JpaRepository<BovinWithPoids, Long>, JpaSpecificationExecutor<BovinWithPoids> {
}