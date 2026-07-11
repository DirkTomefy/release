package mg.bovit.release.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<mg.bovit.release.model.User, Long> {
    java.util.Optional<mg.bovit.release.model.User> findByLogin(String login);   
}
