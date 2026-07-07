package mg.bovit.release.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import mg.bovit.release.model.VenteBovin;

public interface VenteBovinRepository extends JpaRepository<VenteBovin, Long>, JpaSpecificationExecutor<VenteBovin> {
	List<VenteBovin> findByClient_Id(Long clientId);
}
