package mg.bovit.release.repository;

import mg.bovit.release.model.InventaireDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventaireDetailRepository extends JpaRepository<InventaireDetail, Long> {
}