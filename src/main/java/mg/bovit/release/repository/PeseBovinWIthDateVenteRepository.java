package mg.bovit.release.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import mg.bovit.release.model.sqlview.PeseBovinWithDateVente;
public interface PeseBovinWIthDateVenteRepository extends JpaRepository<PeseBovinWithDateVente, Long>, JpaSpecificationExecutor<PeseBovinWithDateVente> {

}
