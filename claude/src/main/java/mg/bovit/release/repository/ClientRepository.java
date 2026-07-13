package mg.bovit.release.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import mg.bovit.release.model.Client;

public interface ClientRepository extends JpaRepository<Client, Long>, JpaSpecificationExecutor<Client> {
	boolean existsByContactIgnoreCase(String contact);

	boolean existsByContactIgnoreCaseAndIdNot(String contact, Long id);
}
