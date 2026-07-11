package mg.bovit.release.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mg.bovit.release.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByLibelle(String libelle);
    List<Role> findAllById(Iterable<Long> ids);
}