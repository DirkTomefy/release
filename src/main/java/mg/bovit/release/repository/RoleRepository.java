package mg.bovit.release.repository;

public interface RoleRepository extends org.springframework.data.jpa.repository.JpaRepository<mg.bovit.release.model.Role, Long> {
    java.util.Optional<mg.bovit.release.model.Role> findByLibelle(String libelle);
}
